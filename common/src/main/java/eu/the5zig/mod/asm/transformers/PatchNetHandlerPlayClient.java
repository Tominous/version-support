/*
 * Copyright (c) 2019 5zig Reborn
 *
 * This file is part of The 5zig Mod
 * The 5zig Mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The 5zig Mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The 5zig Mod.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.the5zig.mod.asm.transformers;

import eu.the5zig.mod.asm.LogUtil;
import eu.the5zig.mod.asm.Names;
import eu.the5zig.util.minecraft.ChatColor;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PatchNetHandlerPlayClient implements IClassTransformer {

	@Override
	public byte[] transform(String s, String s1, byte[] bytes) {
		LogUtil.startClass("NetHandlerPlayClient (%s)", Names.netHandlerPlayClient.getName());

		ClassReader reader = new ClassReader(bytes);
		ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
		ClassPatcher visitor = new ClassPatcher(writer);
		reader.accept(visitor, 0);
		LogUtil.endClass();
		return writer.toByteArray();
	}

	public class ClassPatcher extends ClassVisitor {

		public ClassPatcher(ClassVisitor visitor) {
			super(ASM5, visitor);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if (Names.handleCustomPayload.equals(name, desc)) {
				LogUtil.startMethod(Names.handleCustomPayload.getName() + "(%s)", Names.handleCustomPayload.getDesc());
				return new PatchHandleCustomPayload(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			if (Names.handlePacketPlayerListHeaderFooter.equals(name, desc)) {
				LogUtil.startMethod(Names.handlePacketPlayerListHeaderFooter.getName() + "(%s)", Names.handlePacketPlayerListHeaderFooter.getDesc());
				return new PatchHandlePlayerListHeaderFooter(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			if (Names.handlePacketChat.equals(name, desc)) {
				LogUtil.startMethod(Names.handlePacketChat.getName() + "(%s)", Names.handlePacketChat.getDesc());
				return new PatchHandlePacketChat(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			if (Names.handlePacketSetSlot.equals(name, desc)) {
				LogUtil.startMethod(Names.handlePacketSetSlot.getName() + "(%s)", Names.handlePacketSetSlot.getDesc());
				return new PatchHandlePacketSetSlot(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			if (Names.handlePacketTitle.equals(name, desc)) {
				LogUtil.startMethod(Names.handlePacketTitle.getName() + "(%s)", Names.handlePacketTitle.getDesc());
				return new PatchHandlePacketTitle(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			if (Names.handlePacketTeleport.equals(name, desc)) {
				LogUtil.startMethod(Names.handlePacketTeleport.getName() + "(%s)", Names.handlePacketTeleport.getDesc());
				return new PatchHandlePacketTeleport(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			LogUtil.endMethod();
			return super.visitMethod(access, name, desc, signature, exceptions);
		}

	}

	public class PatchHandleCustomPayload extends MethodVisitor {

		public PatchHandleCustomPayload(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == RETURN) {
				LogUtil.log("payload");
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetPayload.getName(), "a", "()Ljava/lang/String;", false);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetPayload.getName(), "b", "()L" + Names.packetBuffer.getName() + ";", false);
				mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "packetBufferToByteBuf", "(Ljava/lang/Object;)Lio/netty/buffer/ByteBuf;", false);
				mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onCustomPayload", "(Ljava/lang/String;Lio/netty/buffer/ByteBuf;)V", false);
			}
			super.visitInsn(opcode);
		}
	}

	public class PatchHandlePlayerListHeaderFooter extends MethodVisitor {

		public PatchHandlePlayerListHeaderFooter(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == RETURN) {
				LogUtil.log("playerListHeaderFooter");
				mv.visitTypeInsn(NEW, "eu/the5zig/mod/util/TabList");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetHeaderFooter.getName(), "a", "()L" + Names.chatComponent.getName() + ";", false);
				mv.visitMethodInsn(INVOKEINTERFACE, Names.chatComponent.getName(), "d", "()Ljava/lang/String;", true);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetHeaderFooter.getName(), "b", "()L" + Names.chatComponent.getName() + ";", false);
				mv.visitMethodInsn(INVOKEINTERFACE, Names.chatComponent.getName(), "d", "()Ljava/lang/String;", true);
				mv.visitMethodInsn(INVOKESPECIAL, "eu/the5zig/mod/util/TabList", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", false);
				mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onPlayerListHeaderFooter", "(Leu/the5zig/mod/util/TabList;)V", false);
			}
			super.visitInsn(opcode);
		}
	}

	public class PatchHandlePacketChat extends MethodVisitor {

		private int getField = 0;

		public PatchHandlePacketChat(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			if (opcode == GETFIELD && owner.equals(Names.netHandlerPlayClient.getName()) && name.equals("f") && desc.equals("Lave;")) {
				if (getField == 1) {
					LogUtil.log("adding actionBar Proxy");

					mv.visitInsn(POP);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetChat.getName(), "a", "()L" + Names.chatComponent.getName() + ";", false);
					mv.visitMethodInsn(INVOKEINTERFACE, Names.chatComponent.getName(), "d", "()Ljava/lang/String;", true);
					mv.visitLdcInsn(ChatColor.RESET.toString());
					mv.visitLdcInsn("");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;", false);
					mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onActionBar", "(Ljava/lang/String;)Z", false);
					Label l2 = new Label();
					mv.visitJumpInsn(IFEQ, l2);
					mv.visitInsn(RETURN);
					mv.visitLabel(l2);
					mv.visitFrame(F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 0);
				}
				if (getField == 2) {
					LogUtil.log("adding onServerChat Proxy");

					mv.visitInsn(POP);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetChat.getName(), "a", "()L" + Names.chatComponent.getName() + ";", false);
					mv.visitMethodInsn(INVOKEINTERFACE, Names.chatComponent.getName(), "d", "()Ljava/lang/String;", true);
					mv.visitLdcInsn(ChatColor.RESET.toString());
					mv.visitLdcInsn("");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;", false);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetChat.getName(), "a", "()L" + Names.chatComponent.getName() + ";", false);
					mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onChat", "(Ljava/lang/String;Ljava/lang/Object;)Z", false);
					Label l2 = new Label();
					mv.visitJumpInsn(IFEQ, l2);
					mv.visitInsn(RETURN);
					mv.visitLabel(l2);
					mv.visitFrame(F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 0);
				}
				getField++;
			}
			super.visitFieldInsn(opcode, owner, name, desc);
		}

	}

	public class PatchHandlePacketSetSlot extends MethodVisitor {

		private int count = 0;

		public PatchHandlePacketSetSlot(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			if (opcode == INVOKEVIRTUAL && owner.equals(Names.openContainer.getName()) && name.equals("a") && desc.equals("(IL" + Names.itemStack.getName() + ";)V") && !itf) {
				if (count == 1) {
					LogUtil.log("handleInventorySetSlot at c=" + count);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetSetSlot.getName(), "b", "()I", false);
					mv.visitTypeInsn(NEW, "WrappedItemStack");
					mv.visitInsn(DUP);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, Names.packetSetSlot.getName(), "c", "()L" + Names.itemStack.getName() + ";", false);
					mv.visitMethodInsn(INVOKESPECIAL, "WrappedItemStack", "<init>", "(L" + Names.itemStack.getName() + ";)V", false);
					mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onSetSlot", "(ILeu/the5zig/mod/gui/ingame/ItemStack;)V", false);
				}
				count++;
			}
		}
	}

	public class PatchHandlePacketTitle extends MethodVisitor {

		private int returnCount;

		public PatchHandlePacketTitle(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == RETURN) {
				returnCount++;
				if (returnCount == 1) {
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
					mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onTitle", "(Ljava/lang/String;Ljava/lang/String;)V", false);
				} else if (returnCount == 2) {
					mv.visitVarInsn(ALOAD, 3);
					mv.visitVarInsn(ALOAD, 4);
					mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onTitle", "(Ljava/lang/String;Ljava/lang/String;)V", false);
				}
			}
			super.visitInsn(opcode);
		}
	}

	public class PatchHandlePacketTeleport extends MethodVisitor {

		public PatchHandlePacketTeleport(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == RETURN) {
				LogUtil.log("teleport");
				mv.visitVarInsn(DLOAD, 3);
				mv.visitVarInsn(DLOAD, 5);
				mv.visitVarInsn(DLOAD, 7);
				mv.visitVarInsn(FLOAD, 9);
				mv.visitVarInsn(FLOAD, 10);
				mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onTeleport", "(DDDFF)V", false);
			}
			super.visitInsn(opcode);
		}

	}

}

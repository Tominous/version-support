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
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PatchAbstractClientPlayer implements IClassTransformer {

	@Override
	public byte[] transform(String s, String s1, byte[] bytes) {
		LogUtil.startClass("AbstractClientPlayer (%s)", Names.abstractClientPlayer.getName());

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
			if (Names.abstractClientPlayerInit.equals(name, desc)) {
				LogUtil.startMethod(Names.abstractClientPlayerInit.getName() + " " + Names.abstractClientPlayerInit.getDesc());
				return new PatchInit(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			if (Names.getResourceLocation.equals(name, desc)) {
				LogUtil.startMethod(Names.getResourceLocation.getName() + " " + Names.getResourceLocation.getDesc());
				return new PatchGetResourceLocation(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			if (Names.getFOVModifier.equals(name, desc)) {
				LogUtil.startMethod(Names.getFOVModifier.getName() + " " + Names.getFOVModifier.getDesc());
				return new PatchGetFOVModifier(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			LogUtil.endMethod();
			return super.visitMethod(access, name, desc, signature, exceptions);
		}

	}

	public class PatchInit extends MethodVisitor {

		public PatchInit(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == RETURN) {
				LogUtil.log("Init");
				mv.visitVarInsn(ALOAD, 2);
				mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "onAbstractClientPlayerInit", "(Lcom/mojang/authlib/GameProfile;)V", false);
			}
			super.visitInsn(opcode);
		}
	}

	public class PatchGetResourceLocation extends MethodVisitor {

		public PatchGetResourceLocation(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitCode() {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "getCapeLocation", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
			Label l1 = new Label();
			mv.visitJumpInsn(IFNULL, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "getCapeLocation", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
			mv.visitTypeInsn(CHECKCAST, Names.resourceLocation.getName());
			mv.visitInsn(ARETURN);
			mv.visitLabel(l1);
			super.visitCode();
		}
	}

	public class PatchGetFOVModifier extends MethodVisitor {

		public PatchGetFOVModifier(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitCode() {
			super.visitCode();
			LogUtil.log("getFOVModifier");
			mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "isStaticFOV", "()Z", false);
			Label label = new Label();
			mv.visitJumpInsn(IFEQ, label);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "getCustomFOVModifier", "(Ljava/lang/Object;)F", false);
			mv.visitInsn(FRETURN);
			mv.visitLabel(label);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		}
	}

}

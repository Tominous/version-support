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
public class PatchGuiIngameMenu implements IClassTransformer {

	@Override
	public byte[] transform(String s, String s1, byte[] bytes) {
		LogUtil.startClass("GuiIngameMenu (%s)", Names.guiIngameMenu.getName());

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
			if (Names.actionPerformed.equals(name, desc)) {
				LogUtil.startMethod(Names.actionPerformed.getName() + " " + Names.actionPerformed.getDesc());
				return new PatchActionPerformed(cv.visitMethod(access, name, desc, signature, exceptions));
			}
			LogUtil.endMethod();
			return super.visitMethod(access, name, desc, signature, exceptions);
		}

	}

	public class PatchActionPerformed extends MethodVisitor {

		public PatchActionPerformed(MethodVisitor methodVisitor) {
			super(ASM5, methodVisitor);
		}

		@Override
		public void visitCode() {
			super.visitCode();
			LogUtil.log("actionPerformed");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(GETFIELD, Names.guiButton.getName(), "k", "I");
			mv.visitInsn(ICONST_1);
			Label label = new Label();
			mv.visitJumpInsn(IF_ICMPNE, label);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "BytecodeHook", "isConfirmDisconnect", "(Ljava/lang/Object;)Z", false);
			mv.visitJumpInsn(IFEQ, label);
			mv.visitInsn(RETURN);
			mv.visitLabel(label);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

		}

	}

}

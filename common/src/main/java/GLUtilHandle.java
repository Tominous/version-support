/*
 * Original: Copyright (c) 2015-2019 5zig [MIT]
 * Current: Copyright (c) 2019 5zig Reborn [GPLv3+]
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

import eu.the5zig.mod.util.IGLUtil;
import net.minecraft.client.renderer.GlStateManager;

public class GLUtilHandle implements IGLUtil {

	@Override
	public void enableBlend() {
		GlStateManager.enableBlend();
	}

	@Override
	public void disableBlend() {
		GlStateManager.disableBlend();
	}

	@Override
	public void scale(float x, float y, float z) {
		GlStateManager.scale(x, y, z);
	}

	@Override
	public void translate(float x, float y, float z) {
		GlStateManager.translate(x, y, z);
	}

	@Override
	public void color(float r, float g, float b, float a) {
		GlStateManager.color(r, g, b, a);
	}

	@Override
	public void color(float r, float g, float b) {
		color(r, g, b, 1.0f);
	}

	@Override
	public void pushMatrix() {
		GlStateManager.pushMatrix();
	}

	@Override
	public void popMatrix() {
		GlStateManager.popMatrix();
	}

	@Override
	public void matrixMode(int mode) {
		GlStateManager.matrixMode(mode);
	}

	@Override
	public void loadIdentity() {
		GlStateManager.loadIdentity();
	}

	@Override
	public void clear(int i) {
		GlStateManager.clear(i);
	}

	@Override
	public void disableDepth() {
		GlStateManager.disableDepth();
	}

	@Override
	public void enableDepth() {
		GlStateManager.enableDepth();
	}

	@Override
	public void depthMask(boolean b) {
		GlStateManager.depthMask(b);
	}

	@Override
	public void disableLighting() {
		GlStateManager.disableLighting();
	}

	@Override
	public void enableLighting() {
		GlStateManager.enableLighting();
	}

	@Override
	public void disableFog() {
		GlStateManager.disableFog();
	}

	@Override
	public void tryBlendFuncSeparate(int i, int i1, int i2, int i3) {
		GlStateManager.tryBlendFuncSeparate(i, i1, i2, i3);
	}

	@Override
	public void disableAlpha() {
		GlStateManager.disableAlpha();
	}
}

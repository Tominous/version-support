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

import eu.the5zig.mod.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Created by 5zig.
 * All rights reserved (C) 2015
 */
public class StringButton extends Button {

	public StringButton(int id, int x, int y, String label) {
		super(id, x, y, label);
	}

	public StringButton(int id, int x, int y, int width, int height, String label) {
		super(id, x, y, width, height, label);
	}

	@Override
	public void draw(int paramInt1, int paramInt2) {
		if (!this.visible) {
			return;
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		// this.n = ((paramInt1 >= this.h) && (paramInt2 >= this.i) && (paramInt1 < this.h + this.f) && (paramInt2 < this.i + this.g));
		this.hovered = ((paramInt1 >= this.xPosition) && (paramInt2 >= this.yPosition)
				&& (paramInt1 < this.xPosition + this.width) && (paramInt2 < this.yPosition + this.height));

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);

		mouseDragged(Minecraft.getMinecraft(), paramInt1, paramInt2);

		int i2 = 14737632;
		if (!this.enabled) {
			i2 = 10526880;
		} else if (this.hovered) {
			i2 = 16777120;
		}
		Gui.drawCenteredString(this.displayString, this.xPosition + this.width / 2,
				this.yPosition + (this.height - 8) / 2, i2);
	}

}

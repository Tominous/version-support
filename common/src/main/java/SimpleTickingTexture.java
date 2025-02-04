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

import eu.the5zig.mod.MinecraftFactory;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleTickingTexture extends net.minecraft.client.renderer.texture.SimpleTexture implements ITickable {

	private final ResourceLocation resourceLocation;

	private boolean textureUploaded;
	private List<BufferedImage> bufferedImages;
	private List<ResourceLocation> frames;
	private int index;

	public SimpleTickingTexture(ResourceLocation resourceLocation) {
		super(null);
		this.resourceLocation = resourceLocation;
	}

	private void checkTextureUploaded() {
		if (!this.textureUploaded) {
			if (this.frames != null) {
				List<BufferedImage> bufferedImages1 = this.bufferedImages;
				for (int i = 0; i < bufferedImages1.size(); i++) {
					BufferedImage bufferedImage = bufferedImages1.get(i);
					ResourceLocation resourceLocation = new ResourceLocation(this.resourceLocation.getResourceDomain(), this.resourceLocation.getResourcePath() + "_" + i);
					ITextureObject texture = ((Variables) MinecraftFactory.getVars()).getTextureManager().getTexture(resourceLocation);
					SimpleTexture simpleTexture;
					if (texture instanceof SimpleTexture) {
						simpleTexture = (SimpleTexture) texture;
					} else {
						simpleTexture = new SimpleTexture();
						((Variables) MinecraftFactory.getVars()).getTextureManager().loadTexture(resourceLocation, simpleTexture);
					}
					simpleTexture.setBufferedImage(bufferedImage);
					simpleTexture.checkTextureUploaded();
					frames.add(resourceLocation);
				}
				this.textureUploaded = true;
			}
		}
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		if (bufferedImage == null) {
			frames = null;
			index = 0;
			textureUploaded = false;
		} else {
			int parts = bufferedImage.getHeight() / (bufferedImage.getWidth() / 2);
			int partHeight = bufferedImage.getHeight() / parts;
			bufferedImages = new ArrayList<BufferedImage>(parts);
			frames = new ArrayList<ResourceLocation>(parts);
			for (int part = 0; part < parts; part++) {
				bufferedImages.add(bufferedImage.getSubimage(0, part * partHeight, bufferedImage.getWidth(), partHeight));
			}
		}
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {

	}

	@Override
	public void tick() {
		checkTextureUploaded();
		if (textureUploaded && frames.size() > 1) {
			index = (index + 1) % (frames.size());
		}
	}

	public ResourceLocation getCurrentResource() {
		if (!textureUploaded || index < 0 || index >= frames.size()) {
			return null;
		}
		return frames.get(index);
	}
}

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

import com.mojang.authlib.GameProfile;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import eu.the5zig.util.minecraft.ChatColor;

public class WrappedNetworkPlayerInfo implements NetworkPlayerInfo {

	private net.minecraft.client.network.NetworkPlayerInfo wrapped;

	public WrappedNetworkPlayerInfo(net.minecraft.client.network.NetworkPlayerInfo wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public GameProfile getGameProfile() {
		return wrapped.getGameProfile();
	}

	@Override
	public String getDisplayName() {
		String displayName = wrapped.getDisplayName() == null ? null : wrapped.getDisplayName().getFormattedText();
		if (displayName != null && displayName.startsWith(ChatColor.YELLOW + "* ")) {
			displayName = displayName.substring(displayName.indexOf("* ") + 2);
		}
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		if (displayName != null && !ChatColor.stripColor(displayName).equals(getGameProfile().getName())) {
			displayName = ChatColor.YELLOW + "* " + ChatColor.RESET + displayName;
		}
		wrapped.setDisplayName(displayName == null ? null : ChatComponentBuilder.fromLegacyText(displayName));
	}

	@Override
	public int getPing() {
		return wrapped.getResponseTime();
	}

}

package fr.openmc.core.utils;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerUtils {
	public static void sendFadeTitleTeleport(Player player, Location location) {
		if (PlayerSettingsManager.getPlayerSettings(player.getUniqueId()).getSetting(SettingType.TELEPORT_TITLE_FADE)) {
			player.sendTitle(FontImageWrapper.replaceFontImages("§0:tp_effect:"), "§a§lTéléportation...", 20, 10, 10);
			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleportAsync(location);
				}
			}.runTaskLater(OMCPlugin.getInstance(), 10);
		} else {
			player.teleportAsync(location);
		}
	}
}

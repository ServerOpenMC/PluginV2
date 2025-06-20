package fr.openmc.core.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerUtils {
	public static void sendFadeTitleTeleport(Player player, Location location) {
		if (PlayerSettingsManager.getPlayerSettings(player.getUniqueId()).getSetting(SettingType.TELEPORT_TITLE_FADE)) {
			player.sendTitle(PlaceholderAPI.setPlaceholders(player, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
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

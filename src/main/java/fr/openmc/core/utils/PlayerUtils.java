package fr.openmc.core.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PlayerUtils {
	public static void sendFadeTitleTeleport(Player player, Location location) {
		String title = "On vous souhaite bon voyage"; // To be used if the placeholder is not present

		if (OMCPlugin.isPluginEnabled("PlaceholderAPI")) {
			title = PlaceholderAPI.setPlaceholders(player, "§0%img_tp_effect%");
		}
		if ((Boolean) PlayerSettingsManager.getPlayerSettings(player.getUniqueId()).getSetting(SettingType.TELEPORT_TITLE_FADE)) {
			player.sendTitle(title, "§a§lTéléportation...", 20, 10, 10);
			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleport(location);
				}
			}.runTaskLater(OMCPlugin.getInstance(), 10);
		} else {
			player.teleport(location);
		}
	}

	public static void teleportNear(Player player, Location center, double radius) {
		World world = center.getWorld();
		Random random = new Random();

		double angle = random.nextDouble() * 2 * Math.PI;
		double distance = random.nextDouble() * radius;

		double x = center.getX() + Math.cos(angle) * distance;
		double z = center.getZ() + Math.sin(angle) * distance;
		int y = world.getHighestBlockYAt((int) x, (int) z);

		Location target = new Location(world, x, y, z);

		player.teleportAsync(target.add(0.5, 1, 0.5));
	}
}

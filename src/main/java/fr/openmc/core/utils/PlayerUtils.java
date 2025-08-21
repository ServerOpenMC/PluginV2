package fr.openmc.core.utils;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class PlayerUtils {
	public static void sendFadeTitleTeleport(Player player, Location location) {
		if (PlayerSettingsManager.getPlayerSettings(player.getUniqueId()).getSetting(SettingType.TELEPORT_TITLE_FADE)) {
            player.showTitle(Title.title(
                    Component.text(FontImageWrapper.replaceFontImages(":tp_effect:")),
                    Component.text("§a§lTéléportation..."),
                    Title.Times.times(Duration.ofMillis(20 * 50), Duration.ofMillis(10 * 50), Duration.ofMillis(10 * 50))
            ));
			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleportAsync(location);
				}
			}.runTaskLater(OMCPlugin.getInstance(), 14);
		} else {
			player.teleportAsync(location);
		}
	}
}

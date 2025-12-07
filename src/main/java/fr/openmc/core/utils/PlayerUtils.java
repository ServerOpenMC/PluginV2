package fr.openmc.core.utils;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.List;

public class PlayerUtils {
	public static void sendFadeTitleTeleport(Player player, Location location) {
		if (PlayerSettingsManager.getPlayerSettings(player.getUniqueId()).getSetting(SettingType.TELEPORT_TITLE_FADE)) {
            player.showTitle(Title.title(
                    Component.text(FontImageWrapper.replaceFontImages(":tp_effect:")),
                    Component.text("Téléportation...", NamedTextColor.GREEN, TextDecoration.BOLD),
                    Title.Times.times(Duration.ofMillis(20 * 50), Duration.ofMillis(10 * 50), Duration.ofMillis(10 * 50))
            ));
			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleport(location);
				}
			}.runTaskLater(OMCPlugin.getInstance(), 14);
		} else {
			player.teleportAsync(location);
		}
	}

	/**
	 * Fait apparaitre l'effet de gel sur le joueur
	 *
	 * @param player      joueur a donné l'effet
	 * @param freezeTicks nombre de ticks de gel (de 0 à 140)
	 */
	public static void showFreezeEffect(Player player, int freezeTicks) {
		EntityDataAccessor<Integer> FREEZE_TICKS = new EntityDataAccessor<>(7, EntityDataSerializers.INT);

		SynchedEntityData.DataValue<Integer> dataValue =
				new SynchedEntityData.DataValue<>(7, FREEZE_TICKS.serializer(), freezeTicks);

		List<SynchedEntityData.DataValue<?>> dataList = List.of(dataValue);

		ClientboundSetEntityDataPacket packet =
				new ClientboundSetEntityDataPacket(player.getEntityId(), dataList);

		((CraftPlayer) player).getHandle().connection.send(packet);
	}
}

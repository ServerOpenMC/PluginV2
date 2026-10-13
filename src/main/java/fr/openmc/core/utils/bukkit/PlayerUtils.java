package fr.openmc.core.utils.bukkit;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
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
	private PlayerUtils() {}

	public static void sendFadeTitleTeleport(OMCPlayer player, Location location) {
		if (player.settings().shouldShowTeleportFade()) {
            player.showTitle(Title.title(
                    Component.text(FontImageWrapper.replaceFontImages(":tp_effect:")),
					TranslationManager.translation("core.utils.fade_title.teleporting"),
                    Title.Times.times(Duration.ofMillis(20 * 50L), Duration.ofMillis(10 * 50L), Duration.ofMillis(10 * 50L))
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
		EntityDataAccessor<Integer> freezeTicksAccessor = new EntityDataAccessor<>(7, EntityDataSerializers.INT);

		SynchedEntityData.DataValue<Integer> dataValue =
				new SynchedEntityData.DataValue<>(7, freezeTicksAccessor.serializer(), freezeTicks);

		List<SynchedEntityData.DataValue<?>> dataList = List.of(dataValue);

		ClientboundSetEntityDataPacket packet =
				new ClientboundSetEntityDataPacket(player.getEntityId(), dataList);

		((CraftPlayer) player).getHandle().connection.send(packet);
	}
}

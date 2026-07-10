package fr.openmc.core.utils.bukkit;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.combat.CombatCooldownManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    private static final long TELEPORT_DELAY_TICKS = 14L;

    public static boolean sendFadeTitleTeleport(Player player, Location location) {
        if (!canTeleport(player)) {
            return false;
        }

        if (PlayerSettingsManager.getPlayerSettings(player.getUniqueId())
                .getSetting(SettingType.TELEPORT_TITLE_FADE)) {
            player.showTitle(Title.title(
                    Component.text(FontImageWrapper.replaceFontImages(":tp_effect:")),
                    TranslationManager.translation("core.utils.fade_title.teleporting"),
                    Title.Times.times(
                            Duration.ofMillis(1000),
                            Duration.ofMillis(500),
                            Duration.ofMillis(500)
                    )
            ));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (canTeleport(player)) {
                        player.teleport(location);
                    }
                }
            }.runTaskLater(OMCPlugin.getInstance(), TELEPORT_DELAY_TICKS);
        } else {
            player.teleportAsync(location);
        }

        return true;
    }

    private static boolean canTeleport(Player player) {
        long remainingSeconds = CombatCooldownManager.getRemainingSeconds(player.getUniqueId());
        if (remainingSeconds == 0) {
            return true;
        }

        MessagesManager.sendMessage(
                player,
                TranslationManager.translation(
                        "core.utils.teleport.combat_cooldown",
                        Component.text(remainingSeconds).color(NamedTextColor.YELLOW)
                ),
                Prefix.OPENMC,
                MessageType.ERROR,
                true
        );
        return false;
    }

    /**
     * Fait apparaitre l'effet de gel sur le joueur
     *
     * @param player      joueur a donné l'effet
     * @param freezeTicks nombre de ticks de gel (de 0 à 140)
     */
    public static void showFreezeEffect(Player player, int freezeTicks) {
        EntityDataAccessor<Integer> freezeTicksAccessor =
                new EntityDataAccessor<>(7, EntityDataSerializers.INT);

        SynchedEntityData.DataValue<Integer> dataValue =
                new SynchedEntityData.DataValue<>(7, freezeTicksAccessor.serializer(), freezeTicks);

        List<SynchedEntityData.DataValue<?>> dataList = List.of(dataValue);

        ClientboundSetEntityDataPacket packet =
                new ClientboundSetEntityDataPacket(player.getEntityId(), dataList);

        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}

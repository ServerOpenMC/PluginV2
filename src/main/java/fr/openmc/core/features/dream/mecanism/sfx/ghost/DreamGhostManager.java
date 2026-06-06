package fr.openmc.core.features.dream.mecanism.sfx.ghost;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamDimensionManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.mecanism.sfx.ghost.listeners.DreamPlayerEnteredListener;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.nms.SkullNMS;
import fr.openmc.core.utils.nms.entity.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestion des intéractions joueurs dans les reves.
 *
 * Cache tous les joueurs + met juste leur tete d'affiché avec particule
 */
public class DreamGhostManager {
    private static final Map<UUID, PlayerGhost> playerGhost = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(
                new DreamPlayerEnteredListener()
        );
    }

    public static void setupGhost(Player player) {
        int entityId = player.getEntityId() + 100000;

        ArmorStand stand = ArmorStandNMS.createFakeStand(player, entityId, player.getLocation());

        AttributeInstance instance = stand.getAttribute(Attributes.SCALE);
        if (instance == null) return;
        instance.setBaseValue(1.7);

        playerGhost.put(player.getUniqueId(), new PlayerGhost(entityId, stand));

        World world = player.getWorld();

        for (Player other : world.getPlayers()) {
            if (other.equals(player)) continue;

            hidePlayer(other, player);
            hidePlayer(player, other);

            sendGhostTo(other, player, entityId, stand);

            PlayerGhost otherGhost = playerGhost.get(other.getUniqueId());
            if (otherGhost != null) {
                sendGhostTo(player, other, otherGhost.entityId(), otherGhost.stand());
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()
                        || !DreamUtils.isInDreamWorld(player)) {
                    removeGhost(player);
                    this.cancel();
                    return;
                }

                if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

                Location newStand = player.getLocation().subtract(0, 0.5, 0);

                Collection<Player> receivers = newStand.getNearbyEntitiesByType(Player.class, 20).stream()
                        .filter(p -> !p.equals(player)).toList();

                ParticleUtils.spawnCloudParticlesToAll(
                        player.getLocation().add(0, 1.5, 0), Particle.SCULK_SOUL,
                        1, 1, 2, receivers);

                Particle.SHRIEK.builder()
                        .location(newStand)
                        .data(10)
                        .receivers(receivers)
                        .spawn();

                for (Player other : player.getWorld().getPlayers()) {
                    if (!other.equals(player)) {
                        EntityTeleportNMS.sendTeleportPacket(other, entityId, player.getLocation());
                    }
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 2L);
    }

    private static void sendGhostTo(Player receiver, Player ghostOf, int entityId, ArmorStand stand) {
        EntitySpawnNMS.sendSpawnPacket(receiver, EntityType.ARMOR_STAND, entityId, stand.getUUID(), ghostOf.getLocation());
        EntitySpawnNMS.sendMetaDataEntity(receiver, stand);
        EntityEquipmentNMS.sendHelmetPacket(receiver, entityId, SkullNMS.getPlayerSkullNMS(ghostOf));
    }

    public static void removeGhost(Player player) {
        if (!playerGhost.containsKey(player.getUniqueId())) return;
        PlayerGhost ghost = playerGhost.remove(player.getUniqueId());

        if (ghost.stand() == null) return;
        ghost.stand().remove(Entity.RemovalReason.DISCARDED);

        for (Player other : DreamDimensionManager.DREAM_WORLD.getPlayers()) {
            if (other.equals(player)) continue;

            EntityRemoveNMS.sendRemovePacket(other, ghost.entityId());
        }
    }

    public static void hidePlayer(Player receiver, Player toHide) {
        EntityRemoveNMS.sendRemovePacket(receiver, toHide.getEntityId());
    }
}

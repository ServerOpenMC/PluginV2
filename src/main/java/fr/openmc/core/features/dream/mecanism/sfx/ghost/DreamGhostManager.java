package fr.openmc.core.features.dream.mecanism.sfx.ghost;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.mecanism.sfx.ghost.listeners.DreamPlayerEnteredListener;
import fr.openmc.core.features.dream.mecanism.sfx.ghost.listeners.PlayerQuitListener;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.bukkit.SkullUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
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
    private static final Map<UUID, ArmorStand> ghostStands = new HashMap<>();
    private static final NamespacedKey GHOST_KEY = new NamespacedKey(OMCPlugin.getInstance(), "ghost_stand");

    public static void init() {
        OMCPlugin.registerEvents(
                new DreamPlayerEnteredListener(),
                new PlayerQuitListener()
        );
    }

    public static void setupGhost(Player player) {
        World world = player.getWorld();

        for (Player other : world.getPlayers()) {
            if (!other.equals(player)) {
                other.hidePlayer(OMCPlugin.getInstance(), player);
                player.hidePlayer(OMCPlugin.getInstance(), other);
            }
        }

        ArmorStand stand = (ArmorStand) world.spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomNameVisible(false);
        stand.setCanPickupItems(false);
        stand.setMarker(false);
        stand.getPersistentDataContainer().set(GHOST_KEY, PersistentDataType.BOOLEAN, true);

        AttributeInstance instance = stand.getAttribute(Attribute.SCALE);
        if (instance == null) return;

        instance.setBaseValue(1.7);

        ItemStack skull = SkullUtils.getPlayerSkull(player.getUniqueId());
        stand.getEquipment().setHelmet(skull);

        ghostStands.put(player.getUniqueId(), stand);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()
                        || !DreamUtils.isInDreamWorld(player)
                        || !ghostStands.containsKey(player.getUniqueId())) {
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
                        .data(20)
                        .receivers(receivers)
                        .spawn();

                stand.teleport(newStand);
                player.hideEntity(OMCPlugin.getInstance(), stand);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 2L);
    }

    public static void removeGhost(Player player) {
        ArmorStand stand = ghostStands.get(player.getUniqueId());
        if (stand == null) return;
        stand.remove();
        ghostStands.remove(player.getUniqueId());
    }
}

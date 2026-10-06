package fr.openmc.core.features.singularity.sub.wormhole.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.singularity.sub.wormhole.WormHoleManager;
import fr.openmc.core.features.singularity.sub.wormhole.models.WormHoleStage;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SingularityThrowListener implements Listener {
    private static final double MERGE_RADIUS = 4.0;

    @EventHandler(priority = EventPriority.HIGH)
    public void onThrowSingularity(PlayerDropItemEvent event) {
        if (!ItemUtils.isSimilar(DreamItemRegistry.SINGULARITY.getBest(), event.getItemDrop().getItemStack())) return;

        event.setCancelled(true);

        Player player = event.getPlayer();

        if (!player.isOnline()) return;

        ItemStack singularityItem = DreamItemRegistry.SINGULARITY.getBest().clone();

        Location near = player.getLocation();
        ItemDisplay existing = findNearbyWormHole(near);

        if (!ItemUtils.hasEnoughItems(player, singularityItem, 1)) return;
        ItemUtils.removeItemsFromPlayerInventory(player, singularityItem, 1);

        if (existing != null) {
            WormHoleStage stage = WormHoleManager.getStage(existing);
            WormHoleStage nextStage = WormHoleStage.getNextStage(stage);
            if (nextStage == null) return;

            WormHoleManager.setStage(existing, nextStage);
            Location loc = existing.getLocation();
            loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.6f, 1.5f);
            ParticleUtils.spawnRepulsedParticlesSpherical(loc, Particle.SCULK_CHARGE_POP, 10, 7, 30, 20, null);
            return;
        }

        Vector forward = player.getLocation().getDirection().setY(1).normalize().multiply(2);
        Location spawn = player.getLocation().add(forward);

        WormHoleManager.createAndSpawn(player.getUniqueId(), spawn, WormHoleStage.STAGE_1);

        spawn.getWorld().playSound(spawn, Sound.BLOCK_END_PORTAL_SPAWN, 0.8f, 1.5f);
        spawn.getWorld().strikeLightningEffect(spawn);
        ParticleUtils.spawnDispersingParticles(spawn, Particle.SCULK_SOUL, 30, 30, 0.1, null);
    }

    private ItemDisplay findNearbyWormHole(Location location) {
        return location.getNearbyEntitiesByType(ItemDisplay.class, MERGE_RADIUS).stream()
                .filter(e -> OMCRegistry.CUSTOM_MOBS.getMob(e) != null &&
                        OMCRegistry.CUSTOM_MOBS.getMob(e).equals(OMCRegistry.CUSTOM_MOBS.WORM_HOLE.getMob()))
                .findFirst()
                .orElse(null);
    }
}

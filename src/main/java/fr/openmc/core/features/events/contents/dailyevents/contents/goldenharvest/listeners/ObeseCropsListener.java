package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.obesecrops.ObeseCropsRegistry;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.concurrent.ThreadLocalRandom;

public class ObeseCropsListener implements Listener {
    @EventHandler
    public void onCropFullyGrowed(BlockGrowEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent)) return;
        BlockType blockType = event.getBlock().getType().asBlockType();
        KeyBlock keyBlock = KeyBlock.vanilla(blockType);
        if (GoldenHarvestManager.getObeseCropsMapping().get(keyBlock) == null) return;

        BlockData data = event.getNewState().getBlockData();
        if (!(data instanceof Ageable ageable)) return;

        if (ageable.getAge() == ageable.getMaximumAge()) {
            if (ThreadLocalRandom.current().nextDouble() > GoldenHarvestManager.OBESE_CROP_CHANCE) return;

            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                    GoldenHarvestManager.setObeseCrop(event.getBlock()), 1L);

            ParticleUtils.spawnDispersingParticles(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                    Particle.POOF,
                    30,
                    40,
                    0.2,
                    null);
            event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_CREAKING_SPAWN, 1, 0.3f);
        }
    }

    /**
     * Listener qui charge d'enlever le block étant enregistré comme un obese crops
     * Utile pour donner des loots de golden crops si il vient de pousser
     * @param event l'event de destruction de block
     */
    @EventHandler(ignoreCancelled = true)
    public void onObeseCropBreak(BlockBreakEvent event) {
        if (!ObeseCropsRegistry.isObeseCrop(event.getBlock().getLocation())) return;

        ObeseCropsRegistry.unmark(event.getBlock().getLocation());
    }
}

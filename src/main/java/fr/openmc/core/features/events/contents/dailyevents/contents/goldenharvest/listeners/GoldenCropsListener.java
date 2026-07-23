package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.AbondanceArmorManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.obesecrops.ObeseCropsRegistry;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.AbondanceArmorManager.applyDoubleCropsChance;

/**
 * Listener qui prends en charge les loots donnée par les crops et par les obese crops
 */
public class GoldenCropsListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCropBreak(BlockBreakEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent)) return;
        if (ThreadLocalRandom.current().nextDouble() > AbondanceArmorManager.getLuckGoldenCropsModifier(event.getPlayer())) return;

        BlockType blockType = event.getBlock().getType().asBlockType();
        KeyBlock keyBlock = KeyBlock.vanilla(blockType);
        ItemLoot itemLoot = GoldenHarvestManager.getGoldenCropsOnBreakMapping().get(keyBlock);
        if (itemLoot == null) return;

        Set<CustomLoot> loots = itemLoot.run(event.getPlayer(), event.getBlock().getLocation());
        if (loots.isEmpty()) return;

        giveRewards(itemLoot, event.getPlayer(), event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onObeseCropBreak(CustomBlockBreakEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent)) return;
        if (ThreadLocalRandom.current().nextDouble() > GoldenHarvestManager.GOLDEN_CROP_ON_OBESE_CHANCE) return;
        if (!ObeseCropsRegistry.isObeseCrop(event.getBlock().getLocation())) return;

        CustomBlock customBlock = CustomBlock.byItemStack(event.getCustomBlockItem());
        KeyBlock keyBlock = KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.getOrThrow(customBlock.getItemStack()));

        ItemLoot itemLoot = GoldenHarvestManager.getGoldenCropsOnBreakMapping().get(keyBlock);
        if (itemLoot == null) return;

        giveRewards(itemLoot, event.getPlayer(), event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onCropFullyGrowed(BlockGrowEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent)) return;
        BlockType blockType = event.getNewState().getType().asBlockType();
        KeyBlock keyBlock = KeyBlock.vanilla(blockType);
        KeyBlock keyBlockGolden = GoldenHarvestManager.getGoldenCropsOnGrowMapping().get(keyBlock);
        if (keyBlockGolden == null) return;

        if (!(event.getNewState() instanceof Ageable ageable)) return;
        if (ageable.getAge() != ageable.getMaximumAge()) return;

        if (ThreadLocalRandom.current().nextDouble() > GoldenHarvestManager.GOLDEN_CROP_ON_CROP_CHANCE) return;

        CustomBlock customBlock =  keyBlockGolden.getCustomBlock();
        if (customBlock == null) return;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                customBlock.place(event.getBlock().getLocation()), 1L);

        ParticleUtils.spawnDispersingParticles(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                Particle.POOF,
                20,
                40,
                0.3,
                null);
        event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_CREAKING_SPAWN, 1, 0.3f);
    }

    private void giveRewards(ItemLoot itemLoot, Player player, Block block) {
        Collection<CustomLoot> loots = applyDoubleCropsChance(player, itemLoot.run(player, block.getLocation()));
        if (loots.isEmpty()) return;

        player.playSound(player.getLocation(), Sound.ITEM_GOLDEN_DANDELION_USE, 1, 0.3f);
        ParticleUtils.spawnDispersingParticles(
                block.getLocation().add(0.5, 0.5, 0.5),
                Particle.DRIPPING_HONEY,
                10,
                40,
                0.3,
                null);
    }
}

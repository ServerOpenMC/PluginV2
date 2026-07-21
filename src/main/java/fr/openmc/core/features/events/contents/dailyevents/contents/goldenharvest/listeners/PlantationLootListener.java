package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestManager;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Particle;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class PlantationLootListener implements Listener {
    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent)) return;

        BlockType blockType = event.getBlock().getType().asBlockType();
        KeyBlock keyBlock = KeyBlock.vanilla(blockType);
        ItemLoot itemLoot = GoldenHarvestManager.getGoldenCropsOnBreakMapping().get(keyBlock);
        if (itemLoot == null) return;
        if (!(event.getBlock().getBlockData() instanceof Ageable ageable)) return;
        if (ageable.getAge() != ageable.getMaximumAge()) return;

        List<CustomLoot> loots = OMCRegistry.CUSTOM_LOOT_TABLES.CROPS.rollLootsWithoutGuarantee(event.getPlayer());

        if (loots.isEmpty()) return;

        Player player = event.getPlayer();
        MessagesManager.sendMessage(player, TranslationManager.translation(
                "feature.dailyevents.golden_harvest.loot_table.crop_break.message"
        ), Prefix.GOLDEN_HARVEST, MessageType.INFO, false);

        for (CustomLoot loot : loots) {
            loot.sendLootMessage(player, 1);
        }

        ParticleUtils.spawnDispersingParticles(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                Particle.TOTEM_OF_UNDYING,
                10,
                40,
                0.3,
                null);
    }
}

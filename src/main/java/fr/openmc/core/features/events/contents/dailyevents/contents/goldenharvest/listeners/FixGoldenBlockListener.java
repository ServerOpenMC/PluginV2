package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import fr.openmc.core.OMCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Corrige le bug d'un Golden Pumpkin/ Golden Melon qui ne reset pas l'état d'une stem
 */
public class FixGoldenBlockListener implements Listener {
    @EventHandler
    public void onGoldenBlockBreak(CustomBlockBreakEvent event) {
        CustomBlock broken = OMCRegistry.CUSTOM_ITEMS.getOrThrow(event.getNamespacedID()).getCustomBlock();
        if (broken == null) return;

        Material attachedStemType = broken.getNamespacedID().contains("pumpkin")
                ? Material.ATTACHED_PUMPKIN_STEM
                : Material.ATTACHED_MELON_STEM;

        Material stemType = broken.getNamespacedID().contains("pumpkin")
                ? Material.PUMPKIN_STEM
                : Material.MELON_STEM;

        for (BlockFace face : new BlockFace[]{
                BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
            Block relative = event.getBlock().getRelative(face);
            if (!relative.getType().equals(attachedStemType)) continue;

            BlockData data = relative.getBlockData();
            if (!(data instanceof Directional)) continue;

            BlockData newStemData = Bukkit.createBlockData(stemType);
            if (newStemData instanceof Ageable ageable) {
                ageable.setAge(ageable.getMaximumAge());
            }
            relative.setBlockData(newStemData);

            return;
        }
    }
}

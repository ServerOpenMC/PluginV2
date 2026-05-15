package fr.openmc.core.hooks.itemsadder.behaviours;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.openmc.core.hooks.itemsadder.events.IAItemLoadEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class BehaviourUpBlock implements Listener {
    // * String belowBlock, String upBlock
    private static final Map<String, String> UP_BLOCKS = new HashMap<>();

    @EventHandler
    public void onItemLoad(IAItemLoadEvent event) {
        if (hasBehaviourUpBlock(event.getItemConfig())) {
            String belowBlock = event.getItemId();
            String upBlock = event.getItemConfig().getString("behaviours.block.up_block");

            UP_BLOCKS.put(formattedNamespce(event.getNamespace(), belowBlock), upBlock);
        }
    }

    @EventHandler
    public void onBelowBlockPosed(CustomBlockPlaceEvent event) {
        if (!UP_BLOCKS.containsKey(event.getNamespacedID())) return;

        Block upBlock = event.getBlock().getRelative(BlockFace.UP);

        if (upBlock.getType().isAir())
            CustomBlock.place(UP_BLOCKS.get(event.getNamespacedID()), upBlock.getLocation());
    }

    @EventHandler
    public void onBelowBlockBreak(CustomBlockBreakEvent event) {
        if (!UP_BLOCKS.containsKey(event.getNamespacedID())) return;

        Block upBlock = event.getBlock().getRelative(BlockFace.UP);
        CustomBlock upCustomBlock = CustomBlock.byAlreadyPlaced(upBlock);

        if (upCustomBlock != null && UP_BLOCKS.get(event.getNamespacedID()).equals(upCustomBlock.getNamespacedID()))
            upCustomBlock.remove();
    }

    private boolean hasBehaviourUpBlock(ConfigurationSection itemConfig) {
        return itemConfig != null &&
                itemConfig.isConfigurationSection("behaviours.block") &&
                itemConfig.getConfigurationSection("behaviours.block").contains("up_block");
    }

    private String formattedNamespce(String namespace, String itemId) {
        return namespace + ":" + itemId;
    }
}

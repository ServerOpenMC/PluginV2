package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.hooks.itemsadder.behaviours.BehaviourUpBlock;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PeeledObeseCropsListener implements Listener {
    private final Map<KeyBlock, CustomItem> PEELED_OBESE_CROPS_MAPPING = new HashMap<>(Map.of(
            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.OBESE_CARROT), OMCRegistry.CUSTOM_ITEMS.PEELED_OBESE_CARROT,
            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.OBESE_POTATO), OMCRegistry.CUSTOM_ITEMS.PEELED_OBESE_POTATO,
            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.OBESE_BEETROOT), OMCRegistry.CUSTOM_ITEMS.PEELED_OBESE_BEETROOT,
            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.OBESE_ONION), OMCRegistry.CUSTOM_ITEMS.PEELED_OBESE_ONION
    ));

    @EventHandler(ignoreCancelled = true)
    public void onStripObeseCrop(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        ItemStack itemInHand = event.getItem();
        if (itemInHand == null || !Tag.ITEMS_HOES.isTagged(itemInHand.getType())) return;

        CustomItem clickedCustomItem = OMCRegistry.CUSTOM_ITEMS.get(clickedBlock).orElse(null);
        if (clickedCustomItem == null) return;

        KeyBlock keyBlock = KeyBlock.custom(clickedCustomItem);
        CustomItem peeledVariant = PEELED_OBESE_CROPS_MAPPING.get(keyBlock);
        if (peeledVariant == null) return;
        CustomBlock peeledVarientBlock = peeledVariant.getCustomBlock();
        if (peeledVarientBlock == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();

        player.getWorld().playSound(
                clickedBlock.getLocation(),
                Sound.ITEM_AXE_STRIP,
                SoundCategory.MASTER,
                1.0f,
                0.6f
        );

        peeledVarientBlock.place(clickedBlock.getLocation());

        BehaviourUpBlock.onBreak(clickedBlock, keyBlock.getCustomBlock().getNamespacedID());

        ItemUtils.reduceDurability(itemInHand, 5);
    }
}

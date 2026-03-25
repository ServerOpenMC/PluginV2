package fr.openmc.core.listeners;

import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.items.options.BlockBreakableItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;
        if (event.getBlock() == null) return;

        ProtectionsManager.verify(player, event, event.getBlock().getLocation());

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        CustomItem item = CustomItemRegistry.getByItemStack(itemInHand);
        if (item == null) return;

        if (item instanceof BlockBreakableItem breakableItem) breakableItem.onBlockBreak(player, event);
    }

}

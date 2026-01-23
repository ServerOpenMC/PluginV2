package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.models.registry.items.DreamUsableItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DreamItemInteractListener implements Listener {

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!(DreamItemRegistry.getByItemStack(itemInHand) instanceof DreamUsableItem usableItem)) return;

        usableItem.handleInteraction(player, event);
    }

}
package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.models.registry.items.DreamUsableItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DreamItemInteractListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (event.getClickedBlock() == null) return;

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!(DreamItemRegistry.getByItemStack(itemInHand) instanceof DreamUsableItem usableItem)) return;

        usableItem.handleInteraction(player, event);
    }

}
package fr.openmc.core.listeners;

import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.items.usable.CustomUsableItem;
import fr.openmc.core.items.usable.CustomUsableItemRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;
        if (event.getClickedBlock() == null) return;
        ProtectionsManager.verify(player, event, event.getClickedBlock().getLocation());

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        CustomUsableItem usableItem = CustomUsableItemRegistry.getByItemStack(itemInHand);

        if (usableItem != null)
            usableItem.handleInteraction(player, event);
    }

}

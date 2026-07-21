package fr.openmc.api.menulib;

import org.bukkit.event.inventory.InventoryOpenEvent;

public interface OpenMenu {
    /**
     * Handles the event that occurs when a player open the menu's inventory.
     * This method is called whenever an {@link InventoryOpenEvent} is triggered for a menu
     * controlled by this class. Subclasses
     * should implement the logic to respond to the inventory being opened,
     * such as saving data or cleaning up resources.
     *
     * @param event The {@link InventoryOpenEvent} containing details about the open action,
     */
    void onOpen(InventoryOpenEvent event);
}

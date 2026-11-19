package fr.openmc.core.features.events.contents.christmas.adventcalendar.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.events.contents.christmas.adventcalendar.AdventLayouts;
import fr.openmc.core.features.events.contents.christmas.adventcalendar.Door;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChristmasAdventMenu extends Menu {
    /**
     * Constructs a new Menu for the specified player.
     *
     * @param owner The {@link Player} who owns the menu
     */
    public ChristmasAdventMenu(Player owner) {
        super(owner);
    }

    /**
     * Retrieves the name of the menu.
     *
     * @return A non-null {@link Component} representing the name of the menu
     */
    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.christmas.menu.advent_calendar.title");
    }

    /**
     * Retrieves the textures of the menu.
     *
     * @return A {@link String} representing the texture of the menu
     */
    @Override
    public String getTexture() {
        return null;
    }

    /**
     * Retrieves the size of the inventory for the menu.
     *
     * @return An {@link InventorySize} constant representing the size of the inventory.
     */
    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    /**
     * Handles the event that occurs when a player interacts with the menu's inventory.
     * This method is called whenever an {@link InventoryClickEvent} is triggered for a menu
     * controlled by this class. Subclasses should implement the logic to respond
     * to clicks in the inventory, such as handling button functionality or item interactions.
     *
     * @param e The {@link InventoryClickEvent} containing details about the click action,
     *          including the player who clicked, the clicked inventory slot, and other relevant event data.
     */
    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    /**
     * Handles the event that occurs when a player closes the menu's inventory.
     * This method is called whenever an {@link InventoryCloseEvent} is triggered for a menu
     * controlled by this class. Subclasses
     * should implement the logic to respond to the inventory being closed,
     * such as saving data or cleaning up resources.
     *
     * @param event The {@link InventoryCloseEvent} containing details about the close action,
     */
    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    /**
     * Retrieves the content of this menu as a mapping between inventory slot indexes and {@link ItemStack}s.
     * Each entry in the map represents an item stored in a specific slot of the menu's inventory.
     *
     * @return A non-null {@link Map} where the key is an integer representing an inventory slot index,
     * and the value is the {@link ItemStack} present in that slot.
     */
    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        for (Door door : AdventLayouts.DEFAULT) {
            for (int slot : door.getSlots()) {
                ItemStack tile = ItemStack.of(Material.values()[door.day() + 30]);
                content.put(slot, new ItemMenuBuilder(this, tile, meta -> {
                    meta.itemName(Component.text(door.day()));
                }));
            }
        }

        return content;
    }

    /**
     * Retrieves a list of inventory slot indices that can be taken from the menu.
     * These slots are typically used for items that can be moved or removed by the player.
     *
     * @return A non-null list of integers representing the takable inventory slot indices.
     */
    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}

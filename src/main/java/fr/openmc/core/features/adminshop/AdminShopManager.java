package fr.openmc.core.features.adminshop;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.adminshop.menus.AdminShopMenu;
import fr.openmc.core.features.adminshop.menus.ColorVariantsMenu;
import fr.openmc.core.features.adminshop.menus.ConfirmMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Manages the admin shop system including items, categories, and player interactions.
 */
public class AdminShopManager {
    public final Map<String, ShopCategory> categories = new HashMap<>();
    public final Map<String, Map<String, ShopItem>> items = new HashMap<>();
    public final Map<UUID, String> currentCategory = new HashMap<>();
    public final DecimalFormat priceFormat = new DecimalFormat("#,##0.00");
    private final AdminShopYAML adminShopYAML;

    @Getter private static AdminShopManager instance;

    /**
     * Constructs the AdminShopManager and loads the admin shop configuration.
     *
     * @param plugin The main plugin instance.
     */
    public AdminShopManager(OMCPlugin plugin) {
        instance = this;
        this.adminShopYAML = new AdminShopYAML(plugin, this);
        this.adminShopYAML.loadConfig();
    }

    /**
     * Opens the confirmation menu for buying an item.
     *
     * @param player       The player who initiated the action.
     * @param categoryId   The ID of the category.
     * @param itemId       The ID of the item.
     * @param previousMenu The previous menu to return to.
     */
    public void openBuyConfirmMenu(Player player, String categoryId, String itemId, Menu previousMenu) {
        ShopItem item = getItemSafe(player, categoryId, itemId);
        if (item == null) return;

        new ConfirmMenu(player, this, item, true, previousMenu).open();
    }

    /**
     * Opens the confirmation menu for selling an item.
     *
     * @param player       The player who initiated the action.
     * @param categoryId   The ID of the category.
     * @param itemId       The ID of the item.
     * @param previousMenu The previous menu to return to.
     */
    public void openSellConfirmMenu(Player player, String categoryId, String itemId, Menu previousMenu) {
        ShopItem item = getItemSafe(player, categoryId, itemId);
        if (item == null) return;

        if (playerHasItem(player, item.getMaterial(), 1)) {
            sendError(player, "Vous n'avez pas cet item dans votre inventaire !");
            return;
        }

        new ConfirmMenu(player, this, item, false, previousMenu).open();
    }

    /**
     * Handles the purchase of an item by the player.
     *
     * @param player  The player buying the item.
     * @param itemId  The ID of the item.
     * @param amount  The quantity to purchase.
     */
    public void buyItem(Player player, String itemId, int amount) {
        ShopItem item = getCurrentItem(player, itemId);
        if (item == null) return;

        if (!hasEnoughSpace(player, item.getMaterial(), amount)) {
            sendError(player, "Votre inventaire est plein !");
            return;
        }

        if (item.getInitialBuyPrice() <= 0) {
            sendError(player, "Cet item n'est pas à vendre !");
            return;
        }

        double totalPrice = item.getActualBuyPrice() * amount;
        if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), totalPrice)) {
            player.getInventory().addItem(new ItemStack(item.getMaterial(), amount));
            sendInfo(player, "Vous avez acheté " + amount + " " + item.getName() + " pour " + AdminShopUtils.formatPrice(totalPrice));
            adjustPrice(getPlayerCategory(player), itemId, amount, true);
        } else {
            sendError(player, "Vous n'avez pas assez d'argent !");
        }
    }

    /**
     * Handles the selling of an item by the player.
     *
     * @param player  The player selling the item.
     * @param itemId  The ID of the item.
     * @param amount  The quantity to sell.
     */
    public void sellItem(Player player, String itemId, int amount) {
        ShopItem item = getCurrentItem(player, itemId); // Get the item from the current category
        if (item == null) return;

        // Check if the initial sell price is valid
        if (item.getInitialSellPrice() <= 0) {
            sendError(player, "Cet item n'est pas à l'achat !");
            return;
        }

        // Check if the player has enough items to sell
        if (playerHasItem(player, item.getMaterial(), amount)) {
            sendError(player, "Vous n'avez pas assez de " + item.getName() + " à vendre !");
            return;
        }

        double totalPrice = item.getActualSellPrice() * amount; // Calculate the total price for the items
        removeItems(player, item.getMaterial(), amount); // Remove items from the player's inventory
        EconomyManager.getInstance().addBalance(player.getUniqueId(), totalPrice); // Add money to the player's balance
        sendInfo(player, "Vous avez vendu " + amount + " " + item.getName() + " pour " + AdminShopUtils.formatPrice(totalPrice));
        adjustPrice(getPlayerCategory(player), itemId, amount, false); // Adjust the price based on the transaction
    }

    /**
     * Dynamically adjusts the price of an item based on quantity and transaction type.
     *
     * @param categoryId The ID of the category.
     * @param itemId     The ID of the item.
     * @param amount     The quantity bought/sold.
     * @param isBuying   True if buying, false if selling.
     */
    private void adjustPrice(String categoryId, String itemId, int amount, boolean isBuying) {
        ShopItem item = items.getOrDefault(categoryId, Map.of()).get(itemId); // Get the item from the category
        if (item == null) return;

        // Calculate the adjustment factor based on the amount
        double factor = Math.log10(amount + 1) * 0.0001; // Logarithmic scale for adjustment

        double newSell = item.getActualSellPrice() * (isBuying ? 1 + factor : 1 - factor); // Calculate new sell price
        double newBuy = item.getActualBuyPrice() * (isBuying ? 1 + factor : 1 - factor); // Calculate new buy price

        item.setActualSellPrice(Math.max(newSell, item.getInitialSellPrice() * 0.5)); // Set new sell price
        item.setActualBuyPrice(Math.max(newBuy, item.getInitialBuyPrice() * 0.5)); // Set new buy price

        this.adminShopYAML.saveConfig(); // Save the updated configuration
    }

    /**
     * Checks the number of available inventory slots or stack capacity for an item.
     *
     * @param player       The player whose inventory is checked.
     * @param itemToAdd    The material to check for.
     * @param amountToAdd  The amount of items to check.
     * @return -1 if space is available, otherwise number of empty slots.
     */
    private int checkInventorySpace(Player player, Material itemToAdd, int amountToAdd) {
        PlayerInventory inventory = player.getInventory();
        int emptySlots = 0;
        int availableAmount = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            ItemStack itemToAddStack = itemToAdd != null ? new ItemStack(itemToAdd) : null;

            // Check if the slot is empty or if it can stack with the item to add
            if (item == null || item.getType() == Material.AIR) {
                emptySlots++; // Count empty slots
                if (itemToAddStack != null) availableAmount += itemToAddStack.getMaxStackSize(); // Count available space
            } else if (itemToAddStack != null && item.isSimilar(new ItemStack(itemToAddStack))) { // Check if the item is similar to the item to add
                int remainingSpace = item.getMaxStackSize() - item.getAmount(); // Count remaining space in existing stacks
                if (remainingSpace > 0) availableAmount += remainingSpace; // Count available space in existing stacks
            }
        }

        if (itemToAdd != null) {
            if (availableAmount >= amountToAdd) return -1;
            else return emptySlots;
        }

        return emptySlots;
    }

    /**
     * Determines whether a player has enough space in their inventory for a given item.
     *
     * @param player      The player.
     * @param itemToAdd   The material.
     * @param amountToAdd The amount.
     * @return True if there's enough space, false otherwise.
     */
    public boolean hasEnoughSpace(Player player, Material itemToAdd, int amountToAdd) {
        int result = checkInventorySpace(player, itemToAdd, amountToAdd);
        return result == -1 || result > 0;
    }

    /**
     * Determines whether a player has enough item in their inventory for remove this.
     *
     * @param player      The player.
     * @param material   The material.
     * @param amount The amount.
     * @return True if there's enough space, false otherwise.
     */
    public boolean hasEnoughItems(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
                if (count >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the player has fewer than the specified amount of a material.
     *
     * @param player   The player.
     * @param material The material.
     * @param amount   The required amount.
     * @return True if the player has less than the amount, false otherwise.
     */
    private boolean playerHasItem(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) { // Check each item in the player's inventory
            if (item != null && item.getType() == material && (count += item.getAmount()) >= amount) return false;
        }
        return true;
    }

    /**
     * Removes a specified amount of material from the player's inventory.
     *
     * @param player   The player.
     * @param material The material to remove.
     * @param amount   The amount to remove.
     */
    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;

        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack item = player.getInventory().getItem(i); // Get the item in the current slot
            if (item == null || item.getType() != material) continue; // Skip if not the right material

            int amt = item.getAmount();
            if (amt <= remaining) {
                player.getInventory().clear(i); // Remove the item
                remaining -= amt; // Reduce the remaining amount
            } else {
                item.setAmount(amt - remaining); // Reduce the amount
                remaining = 0; // All items removed
            }
        }

        player.updateInventory();
    }

    /**
     * Safely retrieves an item from a category and sends an error if not found.
     *
     * @param player     The player.
     * @param categoryId The category ID.
     * @param itemId     The item ID.
     * @return The ShopItem or null if not found.
     */
    private ShopItem getItemSafe(Player player, String categoryId, String itemId) {
        ShopItem item = items.getOrDefault(categoryId, Map.of()).get(itemId);
        if (item == null) sendError(player, "Item introuvable !");
        return item;
    }

    /**
     * Retrieves the currently selected item from the player's category.
     *
     * @param player The player.
     * @param itemId The item ID.
     * @return The ShopItem or null if not available.
     */
    private ShopItem getCurrentItem(Player player, String itemId) {
        String categoryId = getPlayerCategory(player);
        if (categoryId == null) {
            sendError(player, "Veuillez d'abord ouvrir une catégorie de boutique !");
            return null;
        }
        return getItemSafe(player, categoryId, itemId);
    }

    /**
     * Gets the category currently selected by the player.
     *
     * @param player The player.
     * @return The category ID or null.
     */
    private String getPlayerCategory(Player player) {
        return currentCategory.get(player.getUniqueId());
    }

    /**
     * Sends an error message to a player.
     *
     * @param player  The player.
     * @param message The error message.
     */
    private void sendError(Player player, String message) {
        MessagesManager.sendMessage(player, Component.text(message), Prefix.ADMINSHOP, MessageType.ERROR, true);
    }

    /**
     * Sends an info message to a player (includes currency icon).
     *
     * @param player  The player.
     * @param message The information message.
     */
    private void sendInfo(Player player, String message) {
        MessagesManager.sendMessage(player, Component.text(message), Prefix.ADMINSHOP, MessageType.INFO, true);
    }

    /**
     * Opens the main admin shop menu for a player.
     *
     * @param player The player.
     */
    public void openMainMenu(Player player) {
        new AdminShopMenu(player, this).open();
    }

    /**
     * Opens the menu displaying color variants of a shop item.
     *
     * @param player       The player.
     * @param categoryId   The category ID.
     * @param originalItem The original ShopItem.
     * @param previousMenu The previous menu to return to.
     */
    public void openColorVariantsMenu(Player player, String categoryId, ShopItem originalItem, Menu previousMenu) {
        new ColorVariantsMenu(player, this, categoryId, originalItem, previousMenu).open();
    }

    /**
     * Registers a new item into a category.
     *
     * @param categoryId The category ID.
     * @param itemId     The item ID.
     * @param item       The ShopItem instance.
     */
    public void registerNewItem(String categoryId, String itemId, ShopItem item) {
        items.computeIfAbsent(categoryId, k -> new HashMap<>()).put(itemId, item);
    }

    /**
     * Retrieves all registered shop categories.
     *
     * @return A collection of ShopCategory.
     */
    public Collection<ShopCategory> getCategories() {
        return categories.values();
    }

    /**
     * Gets a specific shop category by ID.
     *
     * @param categoryId The ID of the category.
     * @return The ShopCategory, or null if not found.
     */
    public ShopCategory getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    /**
     * Retrieves all items for a given category.
     *
     * @param categoryId The ID of the category.
     * @return A map of item ID to ShopItem.
     */
    public Map<String, ShopItem> getCategoryItems(String categoryId) {
        return items.getOrDefault(categoryId, Map.of());
    }
}
package fr.openmc.core.features.adminshop;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.features.adminshop.events.BuyEvent;
import fr.openmc.core.features.adminshop.events.SellEvent;
import fr.openmc.core.features.adminshop.menus.*;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Manages the admin shop system including items, categories, and player interactions.
 */
@Credit(developers = {"Axeno"}, graphist = {"Gexary"})
public class AdminShopManager extends Feature implements HasCommands {
    public static final Map<String, ShopCategory> categories = new HashMap<>();
    public static final Map<String, Map<String, ShopItem>> items = new HashMap<>(); // Category -> {ShopID -> ShopItem}
    public static final Map<UUID, String> currentCategory = new HashMap<>();
    public static final DecimalFormat priceFormat = new DecimalFormat("#,##0.00");
    private static AdminShopYAML adminShopYAML;

    /**
     * Initializes the AdminShopManager by loading the configuration.
     */
    @Override
    public void init() {
        adminShopYAML = new AdminShopYAML();
        adminShopYAML.loadConfig();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new AdminShopCommand()
        );
    }

    /**
     * Opens the confirmation menu for buying an item.
     *
     * @param player       The player who initiated the action.
     * @param categoryId   The ID of the category.
     * @param itemId       The ID of the item.
     */
    public static void openBuyConfirmMenu(Player player, String categoryId, String itemId) {
        ShopItem item = getItemSafe(player, categoryId, itemId);
        if (item == null) return;

        new ConfirmMenu(player, item, true).open();
    }

    /**
     * Opens the confirmation menu for selling an item.
     *
     * @param player       The player who initiated the action.
     * @param categoryId   The ID of the category.
     * @param itemId       The ID of the item.
     */
    public static void openSellConfirmMenu(Player player, String categoryId, String itemId) {
        ShopItem item = getItemSafe(player, categoryId, itemId);
        if (item == null) return;

        if (!ItemUtils.hasEnoughItems(player, item.getMaterial(), 1)) {
            sendError(player, TranslationManager.translation("feature.adminshop.have_enough_item"));
            return;
        }

        new ConfirmMenu(player, item, false).open();
    }

    /**
     * Handles the purchase of an item by the player.
     *
     * @param player  The player buying the item.
     * @param itemId  The ID of the item.
     * @param amount  The quantity to purchase.
     */
    public static void buyItem(Player player, String itemId, int amount) {
        ShopItem item = getCurrentItem(player, itemId);
        if (item == null) return;

        if (!ItemUtils.hasEnoughSpace(player, item.getMaterial(), amount)) {
            sendError(player, TranslationManager.translation("feature.adminshop.inventory_full"));
            return;
        }

        if (item.getInitialBuyPrice() <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.item_not_sellable"));
            return;
        }

        double totalPrice = item.getActualBuyPrice() * amount;
        if (EconomyManager.withdrawBalance(player.getUniqueId(), totalPrice, "Achat AdminShop - " + amount + "x " + itemId)) {
            player.getInventory().addItem(new ItemStack(item.getMaterial(), amount));
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new BuyEvent(player, item));
            });
            sendInfo(player, TranslationManager.translation("feature.adminshop.player_buy_item",
                    Component.text(amount), item.getName(), Component.text(AdminShopUtils.formatPrice(totalPrice))
            ));
            adjustPrice(getPlayerCategory(player), itemId, amount, true);
        } else {
            sendError(player, TranslationManager.translation("feature.adminshop.have_enough_money"));
        }
    }

    /**
     * Handles the selling of an item by the player.
     *
     * @param player  The player selling the item.
     * @param itemId  The ID of the item.
     * @param amount  The quantity to sell.
     */
    public static void sellItem(Player player, String itemId, int amount) {
        ShopItem item = getCurrentItem(player, itemId); // Get the item from the current category
        if (item == null) return;

        // Check if the initial sell price is valid
        if (item.getInitialSellPrice() <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.item_not_buyable"));
            return;
        }

        // Check if the player has enough items to sell
        if (!ItemUtils.hasEnoughItems(player, item.getMaterial(), amount)) {
            sendError(player, TranslationManager.translation("feature.adminshop.player_not_enough_item"));
            return;
        }

        double totalPrice = item.getActualSellPrice() * amount; // Calculate the total price for the items
        ItemUtils.removeItemsFromInventory(player, item.getMaterial(), amount); // Remove items from the player's inventory
        EconomyManager.addBalance(player.getUniqueId(), totalPrice, "Vente AdminShop"); // Add money to the player's balance
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new SellEvent(player, item));
        });
        sendInfo(player, TranslationManager.translation("feature.adminshop.player_sell_item",
                Component.text(amount), item.getName(), Component.text(AdminShopUtils.formatPrice(totalPrice))));
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
    private static void adjustPrice(String categoryId, String itemId, int amount, boolean isBuying) {
        ShopItem item = items.getOrDefault(categoryId, Map.of()).get(itemId); // Get the item from the category
        if (item == null) return;

        // Calculate the adjustment factor based on the amount
        double factor = Math.log10(amount + 1) * 0.01; // Logarithmic scale for adjustment

        double newSell = item.getActualSellPrice() * (isBuying ? 1 + factor : 1 - factor); // Calculate new sell price
        double newBuy = item.getActualBuyPrice() * (isBuying ? 1 + factor : 1 - factor); // Calculate new buy price

        item.setActualSellPrice(Math.max(newSell, item.getInitialSellPrice() * 0.5)); // Set new sell price
        item.setActualBuyPrice(Math.max(newBuy, item.getInitialBuyPrice() * 0.5)); // Set new buy price

        adminShopYAML.saveConfig(); // Save the updated configuration
    }

    /**
     * Safely retrieves an item from a category and sends an error if not found.
     *
     * @param player     The player.
     * @param categoryId The category ID.
     * @param itemId     The item ID.
     * @return The ShopItem or null if not found.
     */
    private static ShopItem getItemSafe(Player player, String categoryId, String itemId) {
        ShopItem item = items.getOrDefault(categoryId, Map.of()).get(itemId);
        if (item == null) sendError(player, TranslationManager.translation("feature.adminshop.item_not_found"));
        return item;
    }

    /**
     * Retrieves the currently selected item from the player's category.
     *
     * @param player The player.
     * @param itemId The item ID.
     * @return The ShopItem or null if not available.
     */
    private static ShopItem getCurrentItem(Player player, String itemId) {
        String categoryId = getPlayerCategory(player);
        if (categoryId == null) {
            sendError(player, TranslationManager.translation("feature.adminshop.isnt_in_category"));
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
    private static String getPlayerCategory(Player player) {
        return currentCategory.get(player.getUniqueId());
    }

    /**
     * Sends an error message to a player.
     *
     * @param player  The player.
     * @param message The error message.
     */
    private static void sendError(Player player, Component message) {
        MessagesManager.sendMessage(player, message, Prefix.ADMINSHOP, MessageType.ERROR, true);
    }

    /**
     * Sends an info message to a player.
     *
     * @param player  The player.
     * @param message The information message.
     */
    private static void sendInfo(Player player, Component message) {
        MessagesManager.sendMessage(player, message, Prefix.ADMINSHOP, MessageType.INFO, true);
    }

    /**
     * Opens the main admin shop menu for a player.
     *
     * @param player The player.
     */
    public static void openMainMenu(Player player) {
        new AdminShopMenu(player).open();
    }

    /**
     * Opens the menu displaying color variants of a shop item.
     *
     * @param player       The player.
     * @param categoryId   The category ID.
     * @param originalItem The original ShopItem.
     */
    public static void openColorVariantsMenu(Player player, String categoryId, ShopItem originalItem) {
        new ColorVariantsMenu(player, categoryId, originalItem).open();
    }

    /**
     * Opens the menu displaying leaf variants of a shop item.
     *
     * @param player       The player.
     * @param categoryId   The category ID.
     * @param originalItem The original ShopItem.
     */
    public static void openLeavesVariantsMenu(Player player, String categoryId, ShopItem originalItem) {
        new LeavesVariantsMenu(player, categoryId, originalItem).open();
    }

    /**
     * Opens the menu displaying log variants of a shop item.
     *
     * @param player       The player.
     * @param categoryId   The category ID.
     * @param originalItem The original ShopItem.
     */
    public static void openLogVariantsMenu(Player player, String categoryId, ShopItem originalItem) {
        new LogVariantsMenu(player, categoryId, originalItem).open();
    }

    /**
     * Registers a new item into a category.
     *
     * @param categoryId The category ID.
     * @param itemId     The item ID.
     * @param item       The ShopItem instance.
     */
    public static void registerNewItem(String categoryId, String itemId, ShopItem item) {
        items.computeIfAbsent(categoryId, k -> new HashMap<>()).put(itemId, item);
    }

    /**
     * Retrieves all registered shop categories.
     *
     * @return A collection of ShopCategory.
     */
    public static Collection<ShopCategory> getCategories() {
        return categories.values();
    }

    /**
     * Gets a specific shop category by ID.
     *
     * @param categoryId The ID of the category.
     * @return The ShopCategory, or null if not found.
     */
    public static ShopCategory getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    /**
     * Retrieves all items for a given category.
     *
     * @param categoryId The ID of the category.
     * @return A map of item ID to ShopItem.
     */
    public static Map<String, ShopItem> getCategoryItems(String categoryId) {
        return items.getOrDefault(categoryId, Map.of());
    }
}

package fr.openmc.core.features.adminshop;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.features.adminshop.events.BuyEvent;
import fr.openmc.core.features.adminshop.events.SellEvent;
import fr.openmc.core.features.adminshop.menus.AdminShopCategoryMenu;
import fr.openmc.core.features.adminshop.menus.AdminShopMenu;
import fr.openmc.core.features.adminshop.menus.AdminShopVariantsMenu;
import fr.openmc.core.features.adminshop.menus.category.ColorVariantsMenu;
import fr.openmc.core.features.adminshop.menus.category.LeavesVariantMenu;
import fr.openmc.core.features.adminshop.menus.category.LogVariantMenu;
import fr.openmc.core.features.adminshop.menus.confirm.ConfirmBuyMenu;
import fr.openmc.core.features.adminshop.menus.confirm.ConfirmSellMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Manages the admin shop system including items, categories, and player interactions.
 */
@Credit(developers = {"Axeno", "PuppyTransGirl"}, graphist = {"Gexary"})
public class AdminShopManager extends Feature implements HasCommands {
    private static final double MIN_PRICE_MULTIPLIER = 0.5;
    private static final double MAX_PRICE_MULTIPLIER = 2.5;
    private static final double PRICE_CHANGE_RATE = 0.005;

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
     * @param player     The player who initiated the action.
     * @param categoryId The ID of the category.
     * @param itemId     The ID of the item.
     * @param onFinish   Action run after the purchase is confirmed (typically reopens the menu the player came from).
     *                   If null, the inventory is simply closed.
     */
    public static void openBuyConfirmMenu(Player player, String categoryId, String itemId, Runnable onFinish) {
        ShopItem item = getItemSafe(player, categoryId, itemId);
        if (item == null) return;

        new ConfirmBuyMenu(player, item, onFinish).open();
    }

    /**
     * Opens the confirmation menu for selling an item.
     *
     * @param player     The player who initiated the action.
     * @param categoryId The ID of the category.
     * @param itemId     The ID of the item.
     * @param onFinish   Action run after the sale is confirmed (typically reopens the menu the player came from).
     *                   If null, the inventory is simply closed.
     */
    public static void openSellConfirmMenu(Player player, String categoryId, String itemId, Runnable onFinish) {
        ShopItem item = getItemSafe(player, categoryId, itemId);
        if (item == null) return;

        if (!ItemUtils.hasEnoughItems(player, item.getMaterial(), 1)) {
            sendError(player, TranslationManager.translation("feature.adminshop.have_enough_item"));
            return;
        }

        new ConfirmSellMenu(player, item, onFinish).open();
    }
    /**
     * Handles the purchase of an item by the player.
     *
     * @param player The player buying the item.
     * @param itemId The ID of the item.
     * @param amount The quantity to purchase.
     */
    public static void buyItem(Player player, String itemId, int amount) {
        ShopItem item = getCurrentItem(player, itemId);
        if (item == null) return;

        if (amount <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.amount_sup"));
            return;
        }

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
     * @param player The player selling the item.
     * @param itemId The ID of the item.
     * @param amount The quantity to sell.
     */
    public static void sellItem(Player player, String itemId, int amount) {
        ShopItem item = getCurrentItem(player, itemId); // Get the item from the current category
        if (item == null) return;

        if (amount <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.amount_sup"));
            return;
        }

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
        ItemUtils.removeItemsFromPlayerInventory(player, item.getMaterial(), amount); // Remove items from the player's inventory
        EconomyManager.addBalance(player.getUniqueId(), totalPrice, "Vente AdminShop"); // Add money to the player's balance
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new SellEvent(player, item));
        });
        sendInfo(player, TranslationManager.translation("feature.adminshop.player_sell_item",
                Component.text(amount), item.getName(), Component.text(AdminShopUtils.formatPrice(totalPrice))));
        adjustPrice(getPlayerCategory(player), itemId, amount, false); // Adjust the price based on the transaction
    }

    /**
     * Sells every item of the given type in the player's inventory, without confirmation menu.
     *
     * @param player The player selling the items.
     * @param itemId The ID of the item.
     */
    public static void sellAll(Player player, String itemId) {
        ShopItem item = getCurrentItem(player, itemId);
        if (item == null) return;

        int amount = 0;
        for (ItemStack stack : player.getInventory().getContents())
            if (stack != null && stack.getType() == item.getMaterial())
                amount += stack.getAmount();

        if (amount <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.have_enough_item"));
            return;
        }

        sellItem(player, itemId, amount);
    }

    /**
     * Dynamically adjusts the price of an item based on quantity and transaction type.
     *
     * <p>Buying increases the item's actual buy price, while selling decreases
     * its actual sell price.</p>
     *
     * @param categoryId The ID of the item's category.
     * @param itemId     The ID of the item being traded.
     * @param amount     The total quantity of items traded.
     * @param isBuying   True if the player is buying, false if selling.
     */
    private static void adjustPrice(String categoryId, String itemId, int amount, boolean isBuying) {
        if (amount <= 0) return;

        ShopItem item = items.getOrDefault(categoryId, Map.of()).get(itemId);
        if (item == null) return;

        // Calculate the cumulative multiplier for the entire quantity.
        double multiplier = Math.pow(
                isBuying ? 1 + PRICE_CHANGE_RATE : 1 - PRICE_CHANGE_RATE,
                amount
        );

        if (isBuying) {
            // Increase the buy price based on the quantity purchased.
            double newPrice = item.getActualBuyPrice() * multiplier;
            item.setActualBuyPrice(clamp(newPrice, item.getInitialBuyPrice()));
        } else {
            // Decrease the sell price based on the quantity sold.
            double newPrice = item.getActualSellPrice() * multiplier;
            item.setActualSellPrice(clamp(newPrice, item.getInitialSellPrice()));
        }

        adminShopYAML.saveConfig();
    }

    /**
     * Clamps an item's actual price between its configured minimum and maximum.
     *
     * @param actual  The calculated price before applying the limits.
     * @param initial The item's original configured price.
     * @return The price constrained within the allowed range.
     */
    private static double clamp(double actual, double initial) {
        return Math.clamp(
                actual,
                initial * MIN_PRICE_MULTIPLIER,
                initial * MAX_PRICE_MULTIPLIER
        );
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
     * Opens the menu of a category and marks it as the player's current category.
     * <p>
     * The current category is required afterwards by {@link #buyItem} and {@link #sellItem}
     * to resolve the item, so every way of opening a category menu must go through here.
     *
     * @param player     The player.
     * @param categoryId The ID of the category (must exist, see {@link #findCategory(String)}).
     */
    public static void openCategoryMenu(Player player, String categoryId) {
        currentCategory.put(player.getUniqueId(), categoryId);
        new AdminShopCategoryMenu(player, categoryId).open();
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

    public static void openVariantMenu(Player p, String categoryId, ShopItem item) {
        String name = item.getMaterial().name();

        AdminShopVariantsMenu menu;
        if (name.endsWith("_LEAVES")) menu = new LeavesVariantMenu(p, categoryId, item);
        else if (name.endsWith("_LOG")) menu = new LogVariantMenu(p, categoryId, item);
        else menu = new ColorVariantsMenu(p, categoryId, item);

        menu.open();
    }

    public static void handleShopClick(Player player, String categoryId, ShopItem item,
                                       InventoryClickEvent event, Runnable back) {
        if (event.getClick() == ClickType.SHIFT_LEFT && item.getInitialSellPrice() > 0) {
            sellAll(player, item.getId());
            back.run();
        } else if (event.isLeftClick() && item.getInitialBuyPrice() > 0) {
            openBuyConfirmMenu(player, categoryId, item.getId(), back);
        } else if (event.isRightClick() && item.getInitialSellPrice() > 0) {
            openSellConfirmMenu(player, categoryId, item.getId(), back);
        }
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
     * Finds a shop category from player input, ignoring case.
     *
     * @param input The category ID typed by the player.
     * @return The matching ShopCategory, or null if there is none.
     */
    public static ShopCategory findCategory(String input) {
        if (input == null) return null;

        String id = input.trim();
        ShopCategory exact = categories.get(id);
        if (exact != null) return exact;

        return categories.values().stream()
                .filter(category -> category.id().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
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

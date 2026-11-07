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
 *
 * <p>Every item has two independent dynamic prices (buy and sell):</p>
 * <ul>
 *   <li>Buying an item raises its buy price, selling it lowers its sell price.</li>
 *   <li>Trading an item also moves the other items of the same category (spillover):
 *       buying makes them slightly cheaper to buy, selling makes them slightly better to sell.</li>
 *   <li>Prices slowly drift back to their base value over time (recovery).</li>
 *   <li>A buy price can never end up below its sell price (no buy/resell loop).</li>
 * </ul>
 */
@Credit(developers = {"Axeno", "PuppyTransGirl"}, graphist = {"Gexary"})
public class AdminShopManager extends Feature implements HasCommands {
    /**
     * Lowest buy price, as a multiplier of the base buy price (only reachable through spillover).
     */
    private static final double MIN_BUY_MULTIPLIER = 0.5;
    /**
     * Lowest sell price, as a multiplier of the base sell price.
     */
    private static final double MIN_SELL_MULTIPLIER = 0.2;
    /**
     * Highest price (buy or sell), as a multiplier of the base price.
     */
    private static final double MAX_PRICE_MULTIPLIER = 3;

    /**
     * Stacks of ONE item that must be bought to go from the base buy price to the ceiling.
     */
    private static final int STACKS_TO_REACH_BUY_CEILING = 150;
    /**
     * Stacks of ONE item that must be sold to go from the base sell price to the floor.
     */
    private static final int STACKS_TO_REACH_SELL_FLOOR = 300;

    /**
     * Per-item buy price increase, applied multiplicatively (compound) for every item bought.
     * Derived from {@link #MAX_PRICE_MULTIPLIER} and {@link #STACKS_TO_REACH_BUY_CEILING}, so changing
     * either of them keeps the "number of stacks to reach the limit" correct.
     * Formula: {@code rate = max^(1 / items) - 1}.
     */
    private static final double BUY_PRICE_CHANGE_RATE =
            Math.pow(MAX_PRICE_MULTIPLIER, 1.0 / (STACKS_TO_REACH_BUY_CEILING * 64)) - 1;

    /**
     * Per-item sell price decrease, applied multiplicatively (compound) for every item sold.
     * Derived from {@link #MIN_SELL_MULTIPLIER} and {@link #STACKS_TO_REACH_SELL_FLOOR}.
     * Formula: {@code rate = 1 - min^(1 / items)}.
     */
    private static final double SELL_PRICE_CHANGE_RATE =
            1 - Math.pow(MIN_SELL_MULTIPLIER, 1.0 / (STACKS_TO_REACH_SELL_FLOOR * 64));

    /**
     * Total spillover applied to the rest of the category, as a share of the traded item's rate
     * (0.3 = the whole category together moves 30% as much as the traded item, before price weighting).
     * The effect is split evenly between the other items, so it does not grow with category size.
     */
    private static final double SPILLOVER_RATIO = 0.3;

    /**
     * Share of the gap to the base price recovered at every recovery cycle (0.0029 = 0.29%).
     */
    private static final double RECOVERY_RATE = 0.0029;
    /**
     * Delay between two recovery cycles (10 minutes).
     */
    private static final long RECOVERY_INTERVAL_TICKS = 20L * 60 * 10;
    /**
     * Below this gap (share of the base price), a price snaps back to its base value.
     */
    private static final double RECOVERY_SNAP = 0.001;

    public static final Map<String, ShopCategory> categories = new HashMap<>();
    public static final Map<String, Map<String, ShopItem>> items = new HashMap<>(); // Category -> {ShopID -> ShopItem}
    public static final Map<UUID, String> currentCategory = new HashMap<>();
    public static final DecimalFormat priceFormat = new DecimalFormat("#,##0.00");
    private static AdminShopYAML adminShopYAML;

    /**
     * Initializes the AdminShopManager by loading the configuration
     * and starting the price recovery task.
     */
    @Override
    public void init() {
        adminShopYAML = new AdminShopYAML();
        adminShopYAML.loadConfig();

        Bukkit.getScheduler().runTaskTimer(
                OMCPlugin.getInstance(),
                AdminShopManager::recoverPrices,
                RECOVERY_INTERVAL_TICKS,
                RECOVERY_INTERVAL_TICKS
        );
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
     * Dynamically adjusts prices after a transaction.
     *
     * <ul>
     *   <li>Buying increases the item's actual buy price (up to the ceiling).</li>
     *   <li>Selling decreases the item's actual sell price (down to the floor).</li>
     *   <li>The other items of the category are nudged by {@link #applySpillover}.</li>
     * </ul>
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

        if (isBuying) {
            // Increase the buy price based on the quantity purchased.
            double newPrice = item.getActualBuyPrice() * Math.pow(1 + BUY_PRICE_CHANGE_RATE, amount);
            item.setActualBuyPrice(clamp(newPrice, item.getInitialBuyPrice(), MIN_BUY_MULTIPLIER));
        } else {
            // Decrease the sell price based on the quantity sold.
            double newPrice = item.getActualSellPrice() * Math.pow(1 - SELL_PRICE_CHANGE_RATE, amount);
            item.setActualSellPrice(clamp(newPrice, item.getInitialSellPrice(), MIN_SELL_MULTIPLIER));
        }

        applySpillover(categoryId, item, amount, isBuying);

        adminShopYAML.saveConfig();
    }

    /**
     * Makes the other items of the category slightly more attractive after a transaction.
     *
     * <p>When an item is bought, the other items get a bit cheaper to buy. When it is sold, the other
     * items pay a bit more when sold.</p>
     *
     * <p>The total effect on the category is {@link #SPILLOVER_RATIO} of the traded item's rate, split
     * evenly between the other items tradable on that side, so it does not grow with category size.
     * It is also weighted by relative price: a cheap item barely moves expensive ones, while an
     * expensive item moves cheap ones fully. This prevents farming a worthless item to push up the
     * prices of valuable ones.</p>
     *
     * @param categoryId The ID of the category.
     * @param traded     The item that was just traded.
     * @param amount     The quantity traded.
     * @param isBuying   True if the player bought, false if sold.
     */
    private static void applySpillover(String categoryId, ShopItem traded, int amount, boolean isBuying) {
        double tradedBase = baseFor(traded, isBuying);
        if (tradedBase <= 0) return;

        Collection<ShopItem> category = items.getOrDefault(categoryId, Map.of()).values();

        // Other items tradable on this side: the total effect is split between them.
        long affected = category.stream()
                .filter(other -> other != traded && baseFor(other, isBuying) > 0)
                .count();
        if (affected == 0) return;

        double rate = isBuying ? BUY_PRICE_CHANGE_RATE : SELL_PRICE_CHANGE_RATE;

        for (ShopItem other : category) {
            if (other == traded) continue;

            double otherBase = baseFor(other, isBuying);
            if (otherBase <= 0) continue; // can't be traded on this side

            double weight = Math.min(1.0, tradedBase / otherBase);
            double step = rate * SPILLOVER_RATIO * weight / affected;
            double spill = Math.pow(isBuying ? 1 - step : 1 + step, amount);

            applySpill(other, otherBase, spill, isBuying);
        }
    }

    /**
     * Applies a spillover factor to one item's buy or sell price.
     *
     * <p>The result is clamped to the allowed range of that price. Guard: a buy price is never lowered
     * below the item's sell price, and a sell price is never raised above the item's buy price, so
     * players can't buy and resell the same item for a profit.</p>
     *
     * @param other     The item to adjust.
     * @param otherBase Its base price on the affected side.
     * @param spill     The multiplier to apply.
     * @param isBuying  True to adjust the buy price, false to adjust the sell price.
     */
    private static void applySpill(ShopItem other, double otherBase, double spill, boolean isBuying) {
        if (isBuying) {
            double newBuy = clamp(other.getActualBuyPrice() * spill, otherBase, MIN_BUY_MULTIPLIER);
            double sell = other.getActualSellPrice();
            other.setActualBuyPrice(sell > 0 ? Math.max(newBuy, sell) : newBuy);
        } else {
            double newSell = clamp(other.getActualSellPrice() * spill, otherBase, MIN_SELL_MULTIPLIER);
            double buy = other.getActualBuyPrice();
            other.setActualSellPrice(buy > 0 ? Math.min(newSell, buy) : newSell);
        }
    }

    /**
     * Gets the base (initial) price of an item on the given side.
     *
     * @param item     The item.
     * @param isBuying True for the base buy price, false for the base sell price.
     * @return The base price, or 0 or less if the item can't be traded on that side.
     */
    private static double baseFor(ShopItem item, boolean isBuying) {
        return isBuying ? item.getInitialBuyPrice() : item.getInitialSellPrice();
    }

    /**
     * Moves every price a little closer to its base value.
     *
     * <p>Run periodically (see {@link #init()}). Without it, a price that reached its ceiling or floor
     * would stay there until other players trade the other way around.</p>
     */
    private static void recoverPrices() {
        boolean changed = false;

        for (Map<String, ShopItem> category : items.values()) {
            for (ShopItem item : category.values()) {
                double buy = recover(item.getActualBuyPrice(), item.getInitialBuyPrice());
                double sell = recover(item.getActualSellPrice(), item.getInitialSellPrice());

                if (buy != item.getActualBuyPrice()) {
                    item.setActualBuyPrice(buy);
                    changed = true;
                }
                if (sell != item.getActualSellPrice()) {
                    item.setActualSellPrice(sell);
                    changed = true;
                }
            }
        }

        if (changed) adminShopYAML.saveConfig();
    }

    /**
     * Computes one recovery step for a single price.
     *
     * @param actual  The current price.
     * @param initial The base price.
     * @return The price after recovering {@link #RECOVERY_RATE} of the gap, or the base price if almost there.
     */
    private static double recover(double actual, double initial) {
        double gap = initial - actual;
        if (Math.abs(gap) < initial * RECOVERY_SNAP) return initial;
        return actual + gap * RECOVERY_RATE;
    }

    /**
     * Clamps an item's actual price between its configured minimum and maximum.
     *
     * @param actual        The calculated price before applying the limits.
     * @param initial       The item's original configured price.
     * @param minMultiplier The lowest allowed multiplier (differs for buy and sell prices).
     * @return The price constrained within the allowed range.
     */
    private static double clamp(double actual, double initial, double minMultiplier) {
        return Math.clamp(
                actual,
                initial * minMultiplier,
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
        if (event.getClick() == ClickType.SHIFT_RIGHT && item.getInitialSellPrice() > 0) {
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
        items.computeIfAbsent(categoryId, _ -> new HashMap<>()).put(itemId, item);
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

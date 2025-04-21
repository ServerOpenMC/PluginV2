package fr.openmc.core.features.adminshop;

import dev.xernas.menulib.Menu;
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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;

public class AdminShopManager {
    private final OMCPlugin plugin;
    private FileConfiguration config;
    private final File configFile;
    public final Map<String, ShopCategory> categories = new HashMap<>();
    public final Map<String, Map<String, ShopItem>> items = new HashMap<>();
    public final Map<UUID, String> currentCategory = new HashMap<>();
    public final DecimalFormat priceFormat = new DecimalFormat("#,##0.00");

    @Getter private static AdminShopManager instance;

    public AdminShopManager(OMCPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder() + "/data", "adminshop.yml");
        loadConfig();
    }

    private void loadConfig() {
        if (!configFile.exists()) plugin.saveResource("data/adminshop.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);
        loadCategories();
        loadItems();
    }

    private void loadCategories() {
        categories.clear();
        List<Map<?, ?>> categoryList = config.getMapList("category");

        for (Map<?, ?> categoryMap : categoryList) {
            for (Map.Entry<?, ?> entry : categoryMap.entrySet()) {
                String key = entry.getKey().toString();
                Map<?, ?> section = (Map<?, ?>) entry.getValue();
                categories.put(key, new ShopCategory(
                        key,
                        ChatColor.translateAlternateColorCodes('&', section.get("name").toString()),
                        Material.valueOf(section.get("material").toString()),
                        (int) section.get("position")
                ));
            }
        }
    }

    private void loadItems() {
        items.clear();

        for (String categoryId : categories.keySet()) {
            List<Map<?, ?>> itemList = config.getMapList(categoryId);
            Map<String, ShopItem> categoryItems = new HashMap<>();

            for (Map<?, ?> itemMap : itemList) {
                for (Map.Entry<?, ?> entry : itemMap.entrySet()) {
                    String itemKey = entry.getKey().toString();
                    Map<?, ?> itemSection = (Map<?, ?>) entry.getValue();

                    String name = ChatColor.translateAlternateColorCodes('&', itemSection.get("name").toString());
                    int slot = (int) itemSection.get("slot");
                    Material material = Material.valueOf(itemKey);

                    Map<?, ?> prices = (Map<?, ?>) itemSection.get("price");
                    Map<?, ?> initial = (Map<?, ?>) prices.get("initial");
                    Map<?, ?> actual = (Map<?, ?>) prices.get("actual");

                    categoryItems.put(itemKey, new ShopItem(
                            itemKey, name, material, slot,
                            Double.parseDouble(initial.get("sell").toString()),
                            Double.parseDouble(initial.get("buy").toString()),
                            Double.parseDouble(actual.get("sell").toString()),
                            Double.parseDouble(actual.get("buy").toString())
                    ));
                }
            }

            if (!categoryItems.isEmpty()) items.put(categoryId, categoryItems);
        }
    }

    public void saveConfig() {
        for (var entry : items.entrySet()) {
            String categoryId = entry.getKey();
            List<Map<String, Object>> itemList = new ArrayList<>();

            for (var itemEntry : entry.getValue().entrySet()) {
                ShopItem item = itemEntry.getValue();
                Map<String, Object> itemData = Map.of(
                        "name", item.getName(),
                        "slot", item.getSlot(),
                        "price", Map.of(
                                "initial", Map.of("sell", item.getInitialSellPrice(), "buy", item.getInitialBuyPrice()),
                                "actual", Map.of("sell", item.getActualSellPrice(), "buy", item.getActualBuyPrice())
                        )
                );

                itemList.add(Map.of(itemEntry.getKey(), itemData));
            }

            config.set(categoryId, itemList);
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save shop config", e);
        }
    }

    public void openBuyConfirmMenu(Player player, String categoryId, String itemId, Menu previousMenu) {
        ShopItem item = getItemSafe(player, categoryId, itemId);
        if (item == null) return;

        new ConfirmMenu(player, this, item, true, previousMenu).open();
    }

    public void openSellConfirmMenu(Player player, String categoryId, String itemId, Menu previousMenu) {
        ShopItem item = getItemSafe(player, categoryId, itemId);
        if (item == null) return;

        if (!playerHasItem(player, item.getMaterial(), 1)) {
            sendError(player, "Vous n'avez pas cet item dans votre inventaire !");
            return;
        }

        new ConfirmMenu(player, this, item, false, previousMenu).open();
    }

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
            sendInfo(player, "Vous avez acheté " + amount + " " + item.getName() + " pour " + formatPrice(totalPrice));
            adjustPrice(getPlayerCategory(player), itemId, amount, true);
        } else {
            sendError(player, "Vous n'avez pas assez d'argent !");
        }
    }

    public void sellItem(Player player, String itemId, int amount) {
        ShopItem item = getCurrentItem(player, itemId);
        if (item == null) return;

        if (item.getInitialSellPrice() <= 0) {
            sendError(player, "Cet item n'est pas à l'achat !");
            return;
        }

        if (!playerHasItem(player, item.getMaterial(), amount)) {
            sendError(player, "Vous n'avez pas assez de " + item.getName() + " à vendre !");
            return;
        }

        double totalPrice = item.getActualSellPrice() * amount;
        removeItems(player, item.getMaterial(), amount);
        EconomyManager.getInstance().addBalance(player.getUniqueId(), totalPrice);
        sendInfo(player, "Vous avez vendu " + amount + " " + item.getName() + " pour " + formatPrice(totalPrice));
        adjustPrice(getPlayerCategory(player), itemId, amount, false);
    }

    private void adjustPrice(String categoryId, String itemId, int amount, boolean isBuying) {
        ShopItem item = items.getOrDefault(categoryId, Map.of()).get(itemId);
        if (item == null) return;

        double factor = Math.log10(amount + 1) * 0.0001;

        double newSell = item.getActualSellPrice() * (isBuying ? 1 - factor : 1 + factor);
        double newBuy = item.getActualBuyPrice() * (isBuying ? 1 - factor : 1 + factor);

        item.setActualSellPrice(Math.max(newSell, item.getInitialSellPrice() * 0.5));
        item.setActualBuyPrice(Math.max(newBuy, item.getInitialBuyPrice() * 0.5));

        saveConfig();
    }

    private int checkInventorySpace(Player player, Material itemToAdd, int amountToAdd) {
        PlayerInventory inventory = player.getInventory();
        int emptySlots = 0;
        int availableAmount = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            ItemStack itemToAddStack = itemToAdd != null ? new ItemStack(itemToAdd) : null;

            if (item == null || item.getType() == Material.AIR) {
                emptySlots++;
                if (itemToAddStack != null) availableAmount += itemToAddStack.getMaxStackSize();
            }
            else if (itemToAddStack != null && item.isSimilar(new ItemStack(itemToAddStack))) {
                int remainingSpace = item.getMaxStackSize() - item.getAmount();
                if (remainingSpace > 0) {
                    availableAmount += remainingSpace;
                }
            }
        }

        if (itemToAdd != null) {
            if (availableAmount >= amountToAdd) return -1;
            else return emptySlots;
        }

        return emptySlots;
    }

    public boolean hasEnoughSpace(Player player, Material itemToAdd, int amountToAdd) {
        int result = checkInventorySpace(player, itemToAdd, amountToAdd);
        return result == -1 || result > 0;
    }

    private boolean playerHasItem(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material && (count += item.getAmount()) >= amount) return true;
        }
        return false;
    }

    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;

        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() != material) continue;

            int amt = item.getAmount();
            if (amt <= remaining) {
                player.getInventory().clear(i);
                remaining -= amt;
            } else {
                item.setAmount(amt - remaining);
                remaining = 0;
            }
        }

        player.updateInventory();
    }

    private ShopItem getItemSafe(Player player, String categoryId, String itemId) {
        ShopItem item = items.getOrDefault(categoryId, Map.of()).get(itemId);
        if (item == null) sendError(player, "Item introuvable !");
        return item;
    }

    private ShopItem getCurrentItem(Player player, String itemId) {
        String categoryId = getPlayerCategory(player);
        if (categoryId == null) {
            sendError(player, "Veuillez d'abord ouvrir une catégorie de boutique !");
            return null;
        }
        return getItemSafe(player, categoryId, itemId);
    }

    private String getPlayerCategory(Player player) {
        return currentCategory.get(player.getUniqueId());
    }

    private void sendError(Player player, String message) {
        MessagesManager.sendMessage(player, Component.text(message), Prefix.ADMINSHOP, MessageType.ERROR, true);
    }

    private void sendInfo(Player player, String message) {
        MessagesManager.sendMessage(player, Component.text(message + EconomyManager.getEconomyIcon()), Prefix.ADMINSHOP, MessageType.INFO, true);
    }

    public String formatPrice(double value) {
        return "$" + priceFormat.format(value);
    }

    public void openMainMenu(Player player) {
        new AdminShopMenu(player, this).open();
    }

    public void openColorVariantsMenu(Player player, String categoryId, ShopItem originalItem, Menu previousMenu) {
        new ColorVariantsMenu(player, this, categoryId, originalItem, previousMenu).open();
    }

    public void registerNewItem(String categoryId, String itemId, ShopItem item) {
        items.computeIfAbsent(categoryId, k -> new HashMap<>()).put(itemId, item);
    }

    public Collection<ShopCategory> getCategories() {
        return categories.values();
    }

    public ShopCategory getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    public Map<String, ShopItem> getCategoryItems(String categoryId) {
        return items.getOrDefault(categoryId, Map.of());
    }
}
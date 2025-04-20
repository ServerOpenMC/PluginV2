package fr.openmc.core.features.adminshop;

import dev.xernas.menulib.Menu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.adminshop.menus.AdminShopMenu;
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
        if (!configFile.exists()) {
            plugin.saveResource("data/adminshop.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        loadCategories();
        loadItems();
    }

    private void loadCategories() {
        categories.clear();
        List<Map<?, ?>> categoryList = config.getMapList("category");

        if (categoryList.isEmpty()) {
            plugin.getLogger().warning("No category section found in adminshop.yml");
            return;
        }

        for (Map<?, ?> categoryMap : categoryList) {
            for (Map.Entry<?, ?> entry : categoryMap.entrySet()) {
                String key = entry.getKey().toString();
                Map<?, ?> section = (Map<?, ?>) entry.getValue();

                String name = ChatColor.translateAlternateColorCodes('&', section.get("name").toString());
                Material material = Material.valueOf(section.get("material").toString());
                int position = (int) section.get("position");

                plugin.getLogger().info("Loading category: " + key + " with material: " + material);
                categories.put(key, new ShopCategory(key, name, material, position));
            }
        }
    }

    private void loadItems() {
        items.clear();

        for (String categoryId : categories.keySet()) {
            List<Map<?, ?>> itemList = config.getMapList(categoryId);
            if (itemList.isEmpty()) {
                plugin.getLogger().warning("No items found for category: " + categoryId);
                continue;
            }

            Map<String, ShopItem> categoryItems = new HashMap<>();

            for (Map<?, ?> itemMap : itemList) {
                for (Map.Entry<?, ?> entry : itemMap.entrySet()) {
                    String itemKey = entry.getKey().toString();
                    Map<?, ?> itemSection = (Map<?, ?>) entry.getValue();

                    String name = ChatColor.translateAlternateColorCodes('&', itemSection.get("name").toString());
                    int slot = (int) itemSection.get("slot");
                    Material material = Material.valueOf(itemKey);

                    Map<?, ?> priceSection = (Map<?, ?>) itemSection.get("price");
                    Map<?, ?> initialSection = (Map<?, ?>) priceSection.get("initial");
                    double initialSell = Double.parseDouble(initialSection.get("sell").toString());
                    double initialBuy = Double.parseDouble(initialSection.get("buy").toString());

                    Map<?, ?> actualSection = (Map<?, ?>) priceSection.get("actual");
                    double actualSell = Double.parseDouble(actualSection.get("sell").toString());
                    double actualBuy = Double.parseDouble(actualSection.get("buy").toString());

                    ShopItem item = new ShopItem(itemKey, name, material, slot, initialSell, initialBuy, actualSell, actualBuy);
                    categoryItems.put(itemKey, item);
                }
            }

            items.put(categoryId, categoryItems);
            plugin.getLogger().info("Loaded " + categoryItems.size() + " items for category: " + categoryId);
        }
    }

    public void saveConfig() {
        for (String categoryId : items.keySet()) {
            Map<String, ShopItem> categoryItems = items.get(categoryId);

            List<Map<String, Object>> itemList = new ArrayList<>();

            for (String itemId : categoryItems.keySet()) {
                ShopItem item = categoryItems.get(itemId);

                Map<String, Object> itemData = new HashMap<>();
                itemData.put("name", item.getName());
                itemData.put("slot", item.getSlot());

                Map<String, Object> priceData = new HashMap<>();
                Map<String, Object> initialPrice = new HashMap<>();
                initialPrice.put("sell", item.getInitialSellPrice());
                initialPrice.put("buy", item.getInitialBuyPrice());
                priceData.put("initial", initialPrice);

                Map<String, Object> actualPrice = new HashMap<>();
                actualPrice.put("sell", item.getActualSellPrice());
                actualPrice.put("buy", item.getActualBuyPrice());
                priceData.put("actual", actualPrice);

                itemData.put("price", priceData);

                Map<String, Object> itemEntry = new HashMap<>();
                itemEntry.put(itemId, itemData);

                itemList.add(itemEntry);
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
        Map<String, ShopItem> categoryItems = items.get(categoryId);
        if (categoryItems == null || !categoryItems.containsKey(itemId)) {
            MessagesManager.sendMessage(player, Component.text("Item introuvable !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        ShopItem item = categoryItems.get(itemId);
        ConfirmMenu confirmMenu = new ConfirmMenu(player, this, item, true, previousMenu);
        confirmMenu.open();
    }

    public void openSellConfirmMenu(Player player, String categoryId, String itemId, Menu previousMenu) {
        Map<String, ShopItem> categoryItems = items.get(categoryId);
        if (categoryItems == null || !categoryItems.containsKey(itemId)) {
            MessagesManager.sendMessage(player, Component.text("Item introuvable !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        ShopItem item = categoryItems.get(itemId);

        if (!playerHasItem(player, item.getMaterial(), 1)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas cet item dans votre inventaire !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        ConfirmMenu confirmMenu = new ConfirmMenu(player, this, item, false, previousMenu);
        confirmMenu.open();
    }

    public void buyItem(Player player, String itemId, int amount) {
        String categoryId = currentCategory.get(player.getUniqueId());
        if (categoryId == null) {
            MessagesManager.sendMessage(player, Component.text("Veuillez d'abord ouvrir une catégorie de boutique !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        Map<String, ShopItem> categoryItems = items.get(categoryId);
        if (categoryItems == null || !categoryItems.containsKey(itemId)) {
            MessagesManager.sendMessage(player, Component.text("Item introuvable !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        if (!hasEnoughPlace(player, amount)) {
            MessagesManager.sendMessage(player, Component.text("Votre inventaire est plein !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        ShopItem item = categoryItems.get(itemId);
        double price = item.getActualBuyPrice() * amount;

        if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), price)) {
            ItemStack itemStack = new ItemStack(item.getMaterial(), amount);
            player.getInventory().addItem(itemStack);

            MessagesManager.sendMessage(player, Component.text("Vous avez acheté " + amount + " " + item.getName() + " pour " + priceFormat.format(price) + EconomyManager.getEconomyIcon()), Prefix.ADMINSHOP, MessageType.INFO, true);
            adjustPrice(categoryId, itemId, amount, true);
        } else {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez d'argent !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
        }
    }

    public void sellItem(Player player, String itemId, int amount) {
        String categoryId = currentCategory.get(player.getUniqueId());
        if (categoryId == null) {
            MessagesManager.sendMessage(player, Component.text("Veuillez d'abord ouvrir une catégorie de boutique !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        Map<String, ShopItem> categoryItems = items.get(categoryId);
        if (categoryItems == null || !categoryItems.containsKey(itemId)) {
            MessagesManager.sendMessage(player, Component.text("Item introuvable !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        ShopItem item = categoryItems.get(itemId);
        Material material = item.getMaterial();

        if (!playerHasItem(player, material, amount)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de " + item.getName() + " à vendre !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        if (!hasEnoughPlace(player, amount)) {
            MessagesManager.sendMessage(player, Component.text("Votre inventaire est plein !"), Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        double price = item.getActualSellPrice() * amount;

        removeItems(player, material, amount);

        EconomyManager.getInstance().addBalance(player.getUniqueId(), price);

        MessagesManager.sendMessage(player, Component.text("Vous avez vendu " + amount + " " + item.getName() + " pour " + priceFormat.format(price) + EconomyManager.getEconomyIcon()), Prefix.ADMINSHOP, MessageType.INFO, true);
        adjustPrice(categoryId, itemId, amount, false);
    }

    private boolean hasEnoughPlace(Player player, int amount) {
        int freeSlots = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                freeSlots++;
            }
        }
        return freeSlots >= amount;
    }

    private boolean playerHasItem(Player player, Material material, int amount) {
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

    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;

        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item != null && item.getType() == material) {
                int itemAmount = item.getAmount();
                item.setAmount(itemAmount - remaining);

                if (itemAmount <= remaining) {
                    player.getInventory().remove(item);
                    remaining -= itemAmount;
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
            }
        }

        player.updateInventory();
    }

    private void adjustPrice(String categoryId, String itemId, int amount, boolean isBuying) {
        Map<String, ShopItem> categoryItems = items.get(categoryId);
        if (categoryItems == null || !categoryItems.containsKey(itemId)) {
            return;
        }

        ShopItem item = categoryItems.get(itemId);

        double adjustmentFactor = Math.log10(amount + 1) * 0.0001;

        double newSellPrice;
        double newBuyPrice;
        if (isBuying) {
            newSellPrice = item.getActualSellPrice() * (1 - adjustmentFactor);
            newBuyPrice = item.getActualBuyPrice() * (1 - adjustmentFactor);

        } else {
            newSellPrice = item.getActualSellPrice() * (1 + adjustmentFactor);
            newBuyPrice = item.getActualBuyPrice() * (1 + adjustmentFactor);

            newSellPrice = Math.max(newSellPrice, item.getInitialSellPrice() * 0.5);
            newBuyPrice = Math.max(newBuyPrice, item.getInitialBuyPrice() * 0.5);

        }
        item.setActualSellPrice(newSellPrice);
        item.setActualBuyPrice(newBuyPrice);

        saveConfig();
    }

    public Collection<ShopCategory> getCategories() {
        return categories.values();
    }

    public ShopCategory getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    public Map<String, ShopItem> getCategoryItems(String categoryId) {
        return items.get(categoryId);
    }

    public void openMainMenu(Player player) {
        new AdminShopMenu(player, this).open();
    }
}

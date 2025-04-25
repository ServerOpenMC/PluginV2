package fr.openmc.core.features.adminshop;

import fr.openmc.core.OMCPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Handles loading and saving the admin shop data from a YAML file.
 */
public class AdminShopYAML {
    private final OMCPlugin plugin;
    private FileConfiguration config;
    private final File configFile;
    private final AdminShopManager shopManager;

    /**
     * Constructs the AdminShopYAML manager.
     *
     * @param plugin       The plugin instance.
     * @param shopManager  The admin shop manager instance to populate with data.
     */
    public AdminShopYAML(OMCPlugin plugin, AdminShopManager shopManager) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder() + "/data", "adminshop.yml");
        this.shopManager = shopManager;
    }

    /**
     * Loads the configuration file and populates categories and items.
     */
    public void loadConfig() {
        if (!configFile.exists()) plugin.saveResource("data/adminshop.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);
        loadCategories(); // Load categories first
        loadItems(); // Load items after categories
    }

    /**
     * Loads the shop categories from the YAML configuration and adds them to the manager.
     */
    private void loadCategories() {
        shopManager.categories.clear();
        List<Map<?, ?>> categoryList = config.getMapList("category");

        for (Map<?, ?> categoryMap : categoryList) {
            for (Map.Entry<?, ?> entry : categoryMap.entrySet()) {
                String key = entry.getKey().toString();
                Map<?, ?> section = (Map<?, ?>) entry.getValue();
                shopManager.categories.put(key, new ShopCategory(
                        key,
                        ChatColor.translateAlternateColorCodes('&', section.get("name").toString()),
                        Material.valueOf(section.get("material").toString()),
                        (int) section.get("position")
                ));
            }
        }
    }

    /**
     * Loads all shop items from the YAML configuration and maps them by category.
     */
    private void loadItems() {
        shopManager.items.clear();

        for (String categoryId : shopManager.categories.keySet()) {
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

            if (!categoryItems.isEmpty()) shopManager.items.put(categoryId, categoryItems);
        }
    }

    /**
     * Saves all shop item data back to the YAML configuration file.
     */
    public void saveConfig() {
        for (var entry : shopManager.items.entrySet()) {
            String categoryId = entry.getKey();
            List<Map<String, Object>> itemList = new ArrayList<>();

            for (var itemEntry : entry.getValue().entrySet()) {
                Map<String, Object> itemData = convertShopItemToMap(itemEntry);

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

    /**
     * Converts a {@link ShopItem} to a map suitable for YAML saving.
     *
     * @param itemEntry The map entry of the shop item.
     * @return A map representing the shop item.
     */
    private static @NotNull Map<String, Object> convertShopItemToMap(Map.Entry<String, ShopItem> itemEntry) {
        ShopItem item = itemEntry.getValue();
        return Map.of(
                "name", item.getName(),
                "slot", item.getSlot(),
                "price", Map.of(
                        "initial", Map.of("sell", item.getInitialSellPrice(), "buy", item.getInitialBuyPrice()),
                        "actual", Map.of("sell", item.getActualSellPrice(), "buy", item.getActualBuyPrice())
                )
        );
    }
}

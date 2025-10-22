package fr.openmc.core.features.adminshop;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

/**
 * Handles loading and saving the admin shop data from a YAML file.
 */
public class AdminShopYAML {
    private FileConfiguration config;
    private final File configFile;

    /**
     * Constructs the AdminShopYAML manager.
     */
    public AdminShopYAML() {
        this.configFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "adminshop.yml");
    }

    /**
     * Loads the configuration file and populates categories and items.
     */
    public void loadConfig() {
        if (!configFile.exists()) OMCPlugin.getInstance().saveResource("data/adminshop.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);
        loadCategories(); // Load categories first
        loadItems(); // Load items after categories
    }

    /**
     * Loads the shop categories from the YAML configuration and adds them to the manager.
     */
    private void loadCategories() {
        AdminShopManager.categories.clear();
        List<Map<?, ?>> categoryList = config.getMapList("category");

        for (Map<?, ?> categoryMap : categoryList) {
            for (Map.Entry<?, ?> entry : categoryMap.entrySet()) {
                String key = entry.getKey().toString();
                Map<?, ?> section = (Map<?, ?>) entry.getValue();
                AdminShopManager.categories.put(key, new ShopCategory(
                        key,
                        MiniMessage.miniMessage().deserialize(section.get("name").toString()).decoration(TextDecoration.ITALIC, false),
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
        AdminShopManager.items.clear();

        for (String categoryId : AdminShopManager.categories.keySet()) {
            List<Map<?, ?>> itemList = config.getMapList(categoryId);
            Map<String, ShopItem> categoryItems = new HashMap<>();

            for (Map<?, ?> itemMap : itemList) {
                for (Map.Entry<?, ?> entry : itemMap.entrySet()) {
                    String itemKey = entry.getKey().toString();
                    Map<?, ?> itemSection = (Map<?, ?>) entry.getValue();

                    int slot = (int) itemSection.get("slot");
                    Material material = Material.valueOf(itemKey);
                    Component name = ItemUtils.getItemTranslation(material);

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

            if (!categoryItems.isEmpty()) AdminShopManager.items.put(categoryId, categoryItems);
        }
    }

    /**
     * Saves all shop item data back to the YAML configuration file.
     */
    public void saveConfig() {
        for (var entry : AdminShopManager.items.entrySet()) {
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
            OMCPlugin.getInstance().getSLF4JLogger().warn("Failed to save admin shop configuration file: {}", e.getMessage(), e);
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

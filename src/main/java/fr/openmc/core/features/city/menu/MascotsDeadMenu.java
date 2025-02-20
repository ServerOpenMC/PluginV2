package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.mascots.MascotsLevels;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.mascots.MascotsManager.*;

public class MascotsDeadMenu extends Menu {

    private final String city_uuid;
    List<Component> requiredItemsLore = new ArrayList<>();
    Map<Material, Integer> requiredItems;

    public MascotsDeadMenu(Player owner, String city_uuid) {
        super(owner);
        this.city_uuid = city_uuid;


        Map<Material, Integer> itemCount = new HashMap<>();
        requiredItemsLore.add(Component.text("§bRequière :"));

        loadMascotsConfig();
        String level = mascotsConfig.getString("mascots." + city_uuid + ".level");
        requiredItems = MascotsLevels.valueOf(level).getRequiredItems();

        for (ItemStack item : getOwner().getInventory().getContents()) {
            if (item == null) continue;
            if (requiredItems.containsKey(item.getType())) {
                itemCount.put(item.getType(), itemCount.getOrDefault(item.getType(), 0) + item.getAmount());
            }
        }

        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            Material material = entry.getKey();
            int requiredAmount = entry.getValue();
            int playerAmount = itemCount.getOrDefault(material, 0);

            String color = (playerAmount >= requiredAmount) ? "§a" : "§c";
            requiredItemsLore.add(Component.text(color + material.name() + " (" + playerAmount + "/" + requiredAmount + ")"));
        }
    }

    @Override
    public @NotNull String getName() {
        return "";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> map = new HashMap<>();

        map.put(4, new ItemBuilder(this, Material.APPLE, itemMeta -> {
            itemMeta.setDisplayName("Soigner");
            itemMeta.lore(requiredItemsLore);
        }).setOnClick(inventoryClickEvent -> {
            if (hasRequiredItems(getOwner(), requiredItems)) {
                removeRequiredItems(getOwner(), requiredItems);
                reviveMascots(city_uuid);
                getOwner().closeInventory();
            }
        }));

        return map;
    }

    private boolean hasRequiredItems(Player player, Map<Material, Integer> requiredItems) {
        Inventory inv = player.getInventory();
        Map<Material, Integer> itemCount = new HashMap<>();

        for (ItemStack item : inv.getContents()) {
            if (item != null && requiredItems.containsKey(item.getType())) {
                itemCount.put(item.getType(), itemCount.getOrDefault(item.getType(), 0) + item.getAmount());
            }
        }

        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            if (itemCount.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    private void removeRequiredItems(Player player, Map<Material, Integer> requiredItems) {
        Inventory inv = player.getInventory();

        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            Material material = entry.getKey();
            int amountToRemove = entry.getValue();

            for (ItemStack item : inv.getContents()) {
                if (item != null && item.getType() == material) {
                    int stackSize = item.getAmount();

                    if (stackSize > amountToRemove) {
                        item.setAmount(stackSize - amountToRemove);
                        break;
                    } else {
                        inv.remove(item);
                        amountToRemove -= stackSize;
                    }

                    if (amountToRemove <= 0) {
                        break;
                    }
                }
            }
        }
    }
}
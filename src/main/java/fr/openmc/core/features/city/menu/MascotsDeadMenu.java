package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MascotsDeadMenu extends Menu {

    String city_uuid;

    public MascotsDeadMenu(Player owner, String city_uuid) {
        super(owner);
        this.city_uuid = city_uuid;
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
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }));

        return map;
    }
}

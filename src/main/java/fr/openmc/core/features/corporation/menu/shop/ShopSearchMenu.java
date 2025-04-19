package fr.openmc.core.features.corporation.menu.shop;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.CompanyManager;
import fr.openmc.core.features.corporation.Shop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.kyori.adventure.text.Component;

public class ShopSearchMenu extends PaginatedMenu {

    public ShopSearchMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new java.util.ArrayList<>();



        for (Shop shops : CompanyManager.shops){

            List<Component> loc = new ArrayList<>();
            double x = shops.getBlocksManager().getMultiblock(shops.getUuid()).getStockBlock().getBlockX();
            double y = shops.getBlocksManager().getMultiblock(shops.getUuid()).getStockBlock().getBlockY();
            double z = shops.getBlocksManager().getMultiblock(shops.getUuid()).getStockBlock().getBlockZ();

            loc.add(Component.text("§lLocation : §r x : " + x + " y : " + y + " z : " + z));

            items.add(new ItemBuilder(this, Material.BARREL, itemMeta -> {
                itemMeta.setDisplayName("§lshop :§r" + shops.getName());
                itemMeta.lore(loc);
            }));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        return Map.of();
    }

    @Override
    public @NotNull String getName() {
        return "Search";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}

package fr.openmc.core.disabled.corporation.menu.company;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.disabled.corporation.company.Company;
import fr.openmc.core.disabled.corporation.manager.ShopBlocksManager;
import fr.openmc.core.disabled.corporation.shops.Shop;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopManageMenu extends PaginatedMenu {

    private final Company company;

    public ShopManageMenu(Player owner, Company company) {
        super(owner);
        this.company = company;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return null;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Shop shop : company.getShops()) {

            List<Component> loc = new ArrayList<>();
            double x = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockX();
            double y = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockY();
            double z = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockZ();

            loc.add(Component.text("§lLocation : §r x : " + x + " y : " + y + " z : " + z));

            items.add(new ItemBuilder(this, Material.BARREL , itemMeta -> {
                itemMeta.setDisplayName("§lshop :§r" + shop.getName());
                itemMeta.lore(loc);
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> buttons = new HashMap<>();
        buttons.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§aPage suivante"));
        if ((getPage() == 0 && isLastPage()) || company.getShops().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§cRetour"))
                    .setOnClick(inventoryClickEvent -> new CompanyMenu(getOwner(), company, false).open()));
            buttons.put(50, nextPageButton);
        } else {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        return "Menu du Shop Management";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-11::paginate_company_menu:");
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}

package fr.openmc.core.features.corporation.menu.shop;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.CompanyManager;
import fr.openmc.core.features.corporation.PlayerShopManager;
import fr.openmc.core.features.corporation.Shop;
import fr.openmc.core.features.corporation.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class ShopSalesMenu extends PaginatedMenu {

        private final CompanyManager companyManager = CompanyManager.getInstance();
        private final PlayerShopManager playerShopManager = PlayerShopManager.getInstance();
        private final Shop shop;
        private final int itemIndex;

        public ShopSalesMenu(Player owner, Shop shop, int itemIndex) {
            super(owner);
            this.shop = shop;
            this.itemIndex = itemIndex;
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
            for (ShopItem sale : shop.getSales()) {
                items.add(new ItemBuilder(this, sale.getItem().getType(), itemMeta -> {
                    itemMeta.setDisplayName("§e" + ShopItem.getItemName(sale.getItem()));
                    itemMeta.setLore(List.of(
                            "§7■ Prix : §a" + sale.getPrice() + "€",
                            "§7■ Quantité : §a" + sale.getAmount()
                    ));
                }));
            }
            return items;
        }

        @Override
        public Map<Integer, ItemStack> getButtons() {
            Map<Integer, ItemStack> buttons = new HashMap<>();
            buttons.put(49, new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                    .setCloseButton());
            ItemBuilder nextPageButton = new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> itemMeta.setDisplayName("§aPage suivante"));
            if ((getPage() == 0 && isLastPage()) || shop.getSales().isEmpty()) {
                buttons.put(48, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.setDisplayName("§cRetour"))
                        .setNextMenu(new ShopMenu(getOwner(), companyManager, playerShopManager, shop, itemIndex)));
                buttons.put(50, nextPageButton);
            } else {
                buttons.put(48, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                        .setPreviousPageButton());
                buttons.put(50, nextPageButton.setNextPageButton());
            }
            return buttons;
        }

        @Override
        public @NotNull String getName() {
            return "Ventes de " + shop.getName();
        }

        @Override
        public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

        }
    }

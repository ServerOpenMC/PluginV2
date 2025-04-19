package fr.openmc.core.features.corporation.menu.shop;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.CompanyManager;
import fr.openmc.core.features.corporation.PlayerShopManager;
import fr.openmc.core.features.corporation.Shop;
import fr.openmc.core.features.corporation.ShopItem;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.menu.ConfirmMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopStocksMenu extends PaginatedMenu {

    private final CompanyManager companyManager = CompanyManager.getInstance();
    private final PlayerShopManager playerShopManager = PlayerShopManager.getInstance();
    private final Shop shop;
    private final int itemIndex;
    private ShopItem stock;
    private final List<Component> accetpMsg = new ArrayList<>();
    private final List<Component> denyMsg = new ArrayList<>();

    public ShopStocksMenu(Player owner, Shop shop, int itemIndex) {
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

        accetpMsg.add(Component.text("récupérer"));
        denyMsg.add(Component.text("annuler"));

        for (ShopItem stock : shop.getItems()) {
            this.stock = stock;
            items.add(new ItemBuilder(this, stock.getItem().getType(), itemMeta -> {
                itemMeta.setDisplayName(ChatColor.YELLOW + ShopItem.getItemName(stock.getItem()));
                itemMeta.setLore(List.of(
                        "§7■ Quantitée restante: " + EconomyManager.getInstance().getFormattedNumber(stock.getAmount()),
                        "§7■ Prix de vente (par item) : " + EconomyManager.getInstance().getFormattedNumber(stock.getPricePerItem()),
                        "§7" + (stock.getAmount() > 0 ? "■ Click gauche pour récupérer le stock" : "■ Click gauche pour retirer l'item de la vente")
                ));
            }).setOnClick(inventoryClickEvent -> {
                new ConfirmMenu(getOwner(),this::accept, this::refuse,accetpMsg, denyMsg).open();
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
                    .setNextMenu(new ShopMenu(getOwner(), shop, itemIndex)));
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
        return "Stocks de " + shop.getName();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    private void accept() {
        if (stock.getAmount() > 0) {
            int maxPlace = ItemUtils.getFreePlacesForItem(getOwner(), stock.getItem());
            if (maxPlace>0){
                ItemStack toGive = stock.getItem().clone();
                toGive.setAmount(Math.min(maxPlace, stock.getAmount()));
                int amount = Math.min(maxPlace, stock.getAmount());

                getOwner().getInventory().addItem(toGive);
                stock.setAmount(stock.getAmount() - amount);
                if (stock.getAmount()>0){
                    getOwner().sendMessage("§6Vous avez récupéré §a" + amount + "§6 dans le stock de cet item");
                } else {
                    getOwner().sendMessage("§6Vous avez récupéré le stock restant de cet item");
                }
            } else {
                getOwner().sendMessage("§cVous n'avez pas assez de place");
            }
        } else {
            shop.removeItem(stock);
            getOwner().sendMessage("§aL'item a bien été retiré du shop !");
        }
        getOwner().closeInventory();
    }

    private void refuse() {
        getOwner().closeInventory();
    }
}

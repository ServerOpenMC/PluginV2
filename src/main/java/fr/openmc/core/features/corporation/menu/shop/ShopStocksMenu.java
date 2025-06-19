package fr.openmc.core.features.corporation.menu.shop;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.features.corporation.shops.ShopItem;
import fr.openmc.core.features.corporation.shops.Supply;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.api.ItemsAdderApi;
import fr.openmc.core.utils.api.PapiApi;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShopStocksMenu extends PaginatedMenu {

    private final Shop shop;
    private final int itemIndex;
    private ShopItem stock;
    private final List<Component> accetpMsg = new ArrayList<>();
    private final List<Component> denyMsg = new ArrayList<>();

    public ShopStocksMenu(Player owner, Shop shop, int itemIndex) {
        super(owner);
        this.shop = shop;
        this.itemIndex = itemIndex;

        accetpMsg.add(Component.text("§aRécupérer"));
        denyMsg.add(Component.text("§cAnnuler"));
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new java.util.ArrayList<>();

        for (ShopItem stock : shop.getItems()) {
            items.add(new ItemBuilder(this, stock.getItem().getType(), itemMeta -> {
                itemMeta.displayName(ShopItem.getItemName(stock.getItem()).color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
                itemMeta.setLore(List.of(
                        "§7■ Quantité restante : " + EconomyManager.getFormattedSimplifiedNumber(stock.getAmount()),
                        "§7■ Prix de vente (par item) : " + EconomyManager.getFormattedNumber(stock.getPricePerItem()),
                        "§7" + (stock.getAmount() > 0 ? "■ Click gauche pour récupérer le stock" : "■ Click gauche pour retirer l'item de la vente")
                ));
            }).setOnClick(inventoryClickEvent -> {
                this.stock = stock;
                new ConfirmMenu(getOwner(),this::accept, this::refuse,accetpMsg, denyMsg).open();
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();
        buttons.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, CustomItemRegistry.getByName("menu:next_page").getBest(), itemMeta -> itemMeta.setDisplayName("§aPage suivante"));
        if ((getPage() == 0 && isLastPage()) || shop.getSales().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), itemMeta -> itemMeta.setDisplayName("§cRetour"))
                    .setNextMenu(new ShopMenu(getOwner(), shop, itemIndex)));
            buttons.put(50, nextPageButton);
        } else {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        if (PapiApi.hasPAPI() && ItemsAdderApi.hasItemAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_large_shop_menu%");
        } else {
            return "Stocks de " + shop.getName();
        }
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

    private void accept() {
        Player owner = getOwner();

        if (stock.getAmount() <= 0) {
            shop.removeItem(stock);
            MessagesManager.sendMessage(owner, Component.text("§aL'item a bien été retiré du shop !"), Prefix.SHOP, MessageType.SUCCESS, false);
            owner.closeInventory();
            return;
        }

        int maxPlace = ItemUtils.getFreePlacesForItem(owner, stock.getItem());
        if (maxPlace <= 0) {
            MessagesManager.sendMessage(owner, Component.text("§cVous n'avez pas assez de place"), Prefix.SHOP, MessageType.INFO, false);
            owner.closeInventory();
            return;
        }

        int toTake = Math.min(stock.getAmount(), maxPlace);

        ItemStack toGive = stock.getItem().clone();
        toGive.setAmount(toTake);
        owner.getInventory().addItem(toGive);
        stock.setAmount(stock.getAmount() - toTake);

        if (stock.getAmount() > 0) {
            MessagesManager.sendMessage(owner, Component.text("§6Vous avez récupéré §a" + toTake + "§6 dans le stock de cet item"), Prefix.SHOP, MessageType.SUCCESS, false);
        } else {
            MessagesManager.sendMessage(owner, Component.text("§6Vous avez récupéré le stock restant de cet item"), Prefix.SHOP, MessageType.SUCCESS, false);
        }

        // Mise à jour des suppliers
        int toRemove = toTake;
        Iterator<Map.Entry<Long, Supply>> iterator = shop.getSuppliers().entrySet().iterator();
        while (iterator.hasNext() && toRemove > 0) {
            Map.Entry<Long, Supply> entry = iterator.next();
            Supply supply = entry.getValue();

            if (!supply.getItemId().equals(stock.getItemID())) continue;

            int supplyAmount = supply.getAmount();

            if (supplyAmount <= toRemove) {
                toRemove -= supplyAmount;
                iterator.remove();
            } else {
                supply.setAmount(supplyAmount - toRemove);
                break;
            }
        }

        owner.closeInventory();
    }


    private void refuse() {
        getOwner().closeInventory();
    }
}

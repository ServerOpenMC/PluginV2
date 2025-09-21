package fr.openmc.core.features.corporation.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.features.corporation.manager.PlayerShopManager;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.features.corporation.shops.ShopItem;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopMenu extends Menu {

    private final List<ShopItem> items = new ArrayList<>();
    private final Shop shop;
    private final int itemIndex;

    private int amountToBuy = 1;
    
    public ShopMenu(Player owner) {
        this(owner, PlayerShopManager.getPlayerShop(owner.getUniqueId()));
    }
    
    public ShopMenu(Player owner, Shop shop) {
        this(owner, shop, 0);
    }
    
    public ShopMenu(Player owner, Shop shop, int itemIndex) {
        super(owner);
        this.shop = shop;
        this.itemIndex = itemIndex;
        items.addAll(shop.getItems());
        shop.checkStock();
    }

    @Override
    public @NotNull String getName() {
        return "Menu du shop " + shop.getName();
    }

    @Override
    public String getTexture() {
//            if (shop.getOwner().isCompany()){
//                Company company = shop.getOwner().getCompany();
//                if (company.getAllMembers().contains(getOwner().getUniqueId())){
//                    return FontImageWrapper.replaceFontImages("§r§f:offset_-11::shop_menu:");
//                }
//            }
//            if (!shop.isOwner(getOwner().getUniqueId()))
//                return FontImageWrapper.replaceFontImages("§r§f:offset_-11::shop_menu:");
        return FontImageWrapper.replaceFontImages("§r§f:offset_-11::shop_menu:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();
        
        if (shop.isOwner(getOwner().getUniqueId())) {
            putOwnerItems(content);
        }

        content.put(39, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
            itemMeta.itemName(Component.text("§cItem précédent"));
        }).setOnClick(inventoryClickEvent -> new ShopMenu(getOwner(), shop, onFirstItem() ? itemIndex : itemIndex - 1).open()));

        content.put(41, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
            itemMeta.itemName(Component.text("§aItem suivant"));
        }).setOnClick(inventoryClickEvent -> new ShopMenu(getOwner(), shop, onLastItem() ? itemIndex : itemIndex + 1).open()));

        content.put(40, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
            itemMeta.itemName(Component.text("§7Fermer"));
        }).setCloseButton());

        content.put(19, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:minus_btn").getBest(), itemMeta -> {
            itemMeta.itemName(Component.text("§5Définir à 1"));
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            amountToBuy = 1;
            open();
        }));

        content.put(20, new ItemBuilder(this, CustomItemRegistry.getByName("omc_company:10_btn").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cRetirer 10"));
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            if (amountToBuy == 1) return;
            if (amountToBuy - 10 < 1) {
                amountToBuy = 1;
            } else {
                amountToBuy -= 10;
            }
            open();
        }));
        content.put(21, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:1_btn").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cRetirer 1"));
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            if (amountToBuy == 1) return;
            amountToBuy--;
            open();
        }));

        if (getCurrentItem() != null)
            content.put(22, new ItemBuilder(this, getCurrentItem().getItem(), itemMeta -> {
                itemMeta.displayName(ItemUtils.getItemTranslation(getCurrentItem().getItem()).color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                itemMeta.lore(List.of(
                        Component.text("§7■ Prix: §c" + EconomyManager.getFormattedNumber(getCurrentItem().getPricePerItem() * amountToBuy)),
                        Component.text("§7■ En stock: " + EconomyManager.getFormattedSimplifiedNumber(getCurrentItem().getAmount())),
                        Component.text("§7■ Cliquez pour en acheter §f" + EconomyManager.getFormattedSimplifiedNumber(amountToBuy))
                ));
            }).setOnClick(inventoryClickEvent -> new ConfirmMenu(getOwner(), this::buyAccept, this::refuse, List.of(Component.text("§aAcheter")), List.of(Component.text("§cAnnuler l'achat"))).open()));

        content.put(23, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:1_btn").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aAjouter 1"));
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            amountToBuy = getCurrentItem().getAmount()<=amountToBuy ? getCurrentItem().getAmount() : amountToBuy + 1;
            open();
        }));
        content.put(24, new ItemBuilder(this, CustomItemRegistry.getByName("omc_company:10_btn").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aAjouter 10"));
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            amountToBuy = getCurrentItem().getAmount()<=amountToBuy ? getCurrentItem().getAmount() : amountToBuy + 10;
            open();
        }));

        content.put(25, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:64_btn").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§5Ajouter 64"));
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            if (amountToBuy == 1) amountToBuy = 64;
            else amountToBuy = getCurrentItem().getAmount()<=amountToBuy ? getCurrentItem().getAmount() : amountToBuy + 64;
            open();
        }));

        content.put(44, new ItemBuilder(this, CustomItemRegistry.getByName("omc_company:company_box").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§7Catalogue"));
        }).setOnClick(inventoryClickEvent -> new ShopCatalogueMenu(getOwner(), shop, itemIndex).open()));

        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private void putOwnerItems(Map<Integer, ItemBuilder> content) {

        content.put(0, new ItemBuilder(this, CustomItemRegistry.getByName("omc_homes:omc_homes_icon_bin_red").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§c§lSupprimer le shop"));
        }).setOnClick(inventoryClickEvent -> new ConfirmMenu(getOwner(), this::accept, this::refuse, List.of(Component.text("§aSupprimer")), List.of(Component.text("§cAnnuler la suppression"))).open()));

        content.put(3, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.displayName(Component.text("§a§lVos ventes"));
            itemMeta.lore(List.of(
                    Component.text("§7■ Ventes: §f" + shop.getSales().size()),
                    Component.text("§7■ Cliquer pour voir vos ventes sur ce shop")
            ));
        }).setOnClick(inventoryClickEvent -> new ShopSalesMenu(getOwner(), shop, itemIndex).open()));

        content.put(4, shop.getIcon(this, true));

        content.put(5, new ItemBuilder(this, Material.BARREL, itemMeta -> {
            itemMeta.displayName(Component.text("§6§lVoir les stocks"));
            itemMeta.lore(List.of(
                    Component.text("§7■ Stocks: §f" + shop.getAllItemsAmount()),
                    Component.text("§7■ Cliquer pour voir les stocks de ce shop")
            ));
        }).setOnClick(inventoryClickEvent -> new ShopStocksMenu(getOwner(), shop, itemIndex).open()));

        content.put(8, new ItemBuilder(this, Material.LIME_WOOL, itemMeta -> {
            itemMeta.displayName(Component.text("§aCe shop vous appartient"));
        }));

        content.put(36, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
            itemMeta.displayName(Component.text("§7Comment utiliser les shops"));
        }).setOnClick(inventoryClickEvent -> {

            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            if (meta != null) {
                meta = meta.toBuilder().title(Component.text("Guide des Shops")).author(Component.text("Nocolm"))
                        .addPage(Component.text(
                        """
                                Comment utiliser les shops !
                                
                                §l§6Stock§r :
                                1. Utilisez la commande §d§l/shop sell §r§7<prix> §r en tenant l'item en main
                                2. Ajoutez les items dans le barril §c§l* le raccourci avec les chiffres ne fonctionnera pas *
                                """
                        ))
                        .addPage(Component.text(
                        """
                                3. Ouvrez une fois le shop pour renouveler son stock
                                
                                Et voilà comment utiliser votre shop
                                
                                §6▪ Pour plus d'info : /shop help§r"""
                        )).build();

                book.setItemMeta(meta);
            }
            getOwner().closeInventory();
            getOwner().openBook(book);

            content.remove(44);
        }));
    }

    /**
     * @return the current ShopItem
     */
    private ShopItem getCurrentItem() {
        if (itemIndex < 0 || itemIndex >= items.size()) {
            return null;
        }
        return items.get(itemIndex);
    }

    /**
     * @return true if the menu is on the first item
     */
    private boolean onFirstItem() {
        return itemIndex == 0;
    }

    /**
     * @return true if the menu is on the last item
     */
    private boolean onLastItem() {
        return itemIndex == items.size() - 1;
    }

    private void buyAccept() {
        MethodState buyState = shop.buy(getCurrentItem(), amountToBuy, getOwner());
        if (buyState == MethodState.ERROR) {
            MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas assez d'argent pour acheter cet item"), Prefix.SHOP, MessageType.INFO, false);
            getOwner().closeInventory();
            return;
        }

        if (buyState == MethodState.FAILURE) {
            MessagesManager.sendMessage(getOwner(), Component.text("§cVous ne pouvez pas acheter vos propres items"), Prefix.SHOP, MessageType.INFO, false);
            getOwner().closeInventory();
            return;
        }

        if (buyState == MethodState.WARNING) {
            MessagesManager.sendMessage(getOwner(), Component.text("§cIl n'y a pas assez de stock pour acheter cet item"), Prefix.SHOP, MessageType.INFO, false);
            getOwner().closeInventory();
            return;
        }
        if (buyState == MethodState.SPECIAL) {
            MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas assez de place dans votre inventaire"), Prefix.SHOP, MessageType.INFO, false);
            getOwner().closeInventory();
            return;
        }
        if (buyState == MethodState.ESCAPE) {
            MessagesManager.sendMessage(getOwner(), Component.text("§cErreur lors de l'achat"), Prefix.SHOP, MessageType.INFO, false);
            getOwner().closeInventory();
            return;
        }
        MessagesManager.sendMessage(getOwner(), Component.text("§aVous avez bien acheté " + amountToBuy + " ").append( ItemUtils.getItemTranslation(getCurrentItem().getItem()).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)).append(Component.text(" pour " + (getCurrentItem().getPricePerItem() * amountToBuy) + EconomyManager.getEconomyIcon())), Prefix.SHOP, MessageType.INFO, false);
        getOwner().closeInventory();
    }

    private void accept () {
        MethodState methodState = PlayerShopManager.deleteShop(getOwner().getUniqueId());
        if (methodState == MethodState.WARNING) {
            MessagesManager.sendMessage(getOwner(), Component.text("§cVotre shop n'est pas vide"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        if (methodState == MethodState.ESCAPE) {
            MessagesManager.sendMessage(getOwner(), Component.text("§cCaisse introuvable (appelez un admin)"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(getOwner(), Component.text("§aVotre shop a bien été supprimé !"), Prefix.SHOP, MessageType.INFO, false);
        MessagesManager.sendMessage(getOwner(), Component.text("§6[Shop]§a +400" + EconomyManager.getEconomyIcon() + " de remboursés sur votre compte personnel"), Prefix.SHOP, MessageType.INFO, false);
        getOwner().closeInventory();
    }

    private void refuse() {
        getOwner().closeInventory();
    }
}

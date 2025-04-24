package fr.openmc.core.features.corporation.menu.shop;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.*;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.menu.ConfirmMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopMenu extends Menu {

    private final List<ShopItem> items = new ArrayList<>();
    private final CompanyManager companyManager = CompanyManager.getInstance();
    private final PlayerShopManager playerShopManager = PlayerShopManager.getInstance();
    private final Shop shop;
    private final int itemIndex;
    private final List<Component> accetpBuyMsg = new ArrayList<>();
    private final List<Component> denyBuyMsg = new ArrayList<>();
    private final List<Component> accetpMsg = new ArrayList<>();
    private final List<Component> denyMsg = new ArrayList<>();

    private int amountToBuy = 1;

    public ShopMenu(Player owner, Shop shop, int itemIndex) {
        super(owner);
        this.shop = shop;
        this.itemIndex = itemIndex;
        items.addAll(shop.getItems());
        Shop.checkStock(shop);
    }

    @Override
    public @NotNull String getName() {
        return shop.getName();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        if (!shop.isOwner(getOwner().getUniqueId()))
            return InventorySize.LARGE;
        return InventorySize.LARGER;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Company company = null;

        int previousItemSlot;
        int nextItemSlot;
        int closeMenuSlot;

        int purpleSetOne;
        int redRemoveTen;
        int redRemoveOne;
        int itemSlot;
        int greenAddOne;
        int greenAddTen;
        int purpleAddSixtyFour;

        int catalogue;

        boolean ownerItem = false;

        if (shop.getOwner().isCompany()){
            company = shop.getOwner().getCompany();
        }
        if (company == null && shop.isOwner(getOwner().getUniqueId())) {
            previousItemSlot = 39;
            nextItemSlot = 41;
            closeMenuSlot = 40;
            purpleSetOne = 19;
            redRemoveTen = 20;
            redRemoveOne = 21;
            itemSlot = 22;
            greenAddOne = 23;
            greenAddTen = 24;
            purpleAddSixtyFour = 25;
            catalogue = 44;
            ownerItem = true;
        } else if (company != null && company.getAllMembers().contains(getOwner().getUniqueId())) {
            previousItemSlot = 39;
            nextItemSlot = 41;
            closeMenuSlot = 40;
            purpleSetOne = 19;
            redRemoveTen = 20;
            redRemoveOne = 21;
            itemSlot = 22;
            greenAddOne = 23;
            greenAddTen = 24;
            purpleAddSixtyFour = 25;
            catalogue = 44;
            ownerItem = true;
        } else {
            previousItemSlot = 30;
            nextItemSlot = 32;
            closeMenuSlot = 31;
            purpleSetOne = 10;
            redRemoveTen = 11;
            redRemoveOne = 12;
            itemSlot = 13;
            greenAddOne = 14;
            greenAddTen = 15;
            purpleAddSixtyFour = 16;
            catalogue = 35;
        }

        accetpBuyMsg.add(Component.text("§aAcheter"));
        denyBuyMsg.add(Component.text("§cAnnuler l'achat"));
        accetpMsg.add(Component.text("§aSupprimer"));
        denyMsg.add(Component.text("§cAnnuler la suppression"));

        Map<Integer, ItemStack> content = fill(Material.GRAY_STAINED_GLASS_PANE);

        content.put(previousItemSlot, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> {
            itemMeta.setDisplayName("§cItem précédent");
        }).setNextMenu(new ShopMenu(getOwner(), shop, onFirstItem() ? itemIndex : itemIndex - 1)));

        content.put(nextItemSlot, new ItemBuilder(this, Material.LIME_CONCRETE, itemMeta -> {
            itemMeta.setDisplayName("§aItem suivant");
        }).setNextMenu(new ShopMenu(getOwner(), shop, onLastItem() ? itemIndex : itemIndex + 1)));

        content.put(closeMenuSlot, new ItemBuilder(this, Material.BARRIER, itemMeta -> {
            itemMeta.setDisplayName("§7Fermer");
        }).setCloseButton());

        if (ownerItem)
            putOwnerItems(content);
        content.put(purpleSetOne, new ItemBuilder(this, Material.PURPLE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§5Définir à 1");
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            amountToBuy = 1;
            open();
        }));

        content.put(redRemoveTen, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§cRetirer 10");
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

        content.put(redRemoveOne, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§cRetirer 1");
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            if (amountToBuy == 1) return;
            amountToBuy--;
            open();
        }));

        if (getCurrentItem() != null)

            content.put(itemSlot, new ItemBuilder(this, getCurrentItem().getItem(), itemMeta -> {
                itemMeta.setDisplayName("§l§f" + ItemUtils.getItemTranslation(getCurrentItem().getItem()));
                List<String> lore = new ArrayList<>();
                lore.add("§7■ Prix: §c" + (getCurrentItem().getPricePerItem() * amountToBuy) + "€");
                lore.add("§7■ En stock: " + EconomyManager.getInstance().getFormattedNumber(getCurrentItem().getAmount()));
                OMCPlugin.getInstance().getLogger().info("" + getCurrentItem().getAmount());
                lore.add("§7■ Cliquez pour en acheter §f" + amountToBuy);
                itemMeta.setLore(lore);
            }).setNextMenu(new ConfirmMenu(getOwner(), this::buyAccept, this::refuse, accetpBuyMsg, denyBuyMsg)));

        content.put(greenAddOne, new ItemBuilder(this, Material.LIME_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§aAjouter 1");
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            amountToBuy++;
            open();
        }));

        content.put(greenAddTen, new ItemBuilder(this, Material.LIME_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§aAjouter 10");
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            amountToBuy += 10;
            open();
        }));

        content.put(purpleAddSixtyFour, new ItemBuilder(this, Material.PURPLE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§5Ajouter 64");
        }).setOnClick(inventoryClickEvent -> {
            if (getCurrentItem() == null) return;
            if (amountToBuy == 1) amountToBuy = 64;
            else amountToBuy += 64;
            open();
        }));

        content.put(catalogue, new ItemBuilder(this, Material.CHEST, itemMeta -> {
            itemMeta.setDisplayName("§7Catalogue");
        }).setNextMenu(new ShopCatalogueMenu(getOwner(), shop, itemIndex)));

        return content;
    }

    private void putOwnerItems(Map<Integer, ItemStack> content) {

        content.put(0, new ItemBuilder(this, Material.RED_DYE, itemMeta -> {
            itemMeta.setDisplayName("§c§lSupprimer le shop");
        }).setNextMenu(new ConfirmMenu(getOwner(), this::accept, this::refuse, accetpMsg, denyMsg)));

        content.put(3, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.setDisplayName("§a§lVos ventes");
            List<String> lore = new ArrayList<>();
            lore.add("§7■ Ventes: §f" + shop.getSales().size());
            lore.add("§7■ Cliquer pour voir vos ventes sur ce shop");
            itemMeta.setLore(lore);
        }).setNextMenu(new ShopSalesMenu(getOwner(), shop, itemIndex)));

        content.put(4, shop.getIcon(this, true));

        content.put(5, new ItemBuilder(this, Material.BARREL, itemMeta -> {
            itemMeta.setDisplayName("§6§lVoir les stocks");
            List<String> lore = new ArrayList<>();
            lore.add("§7■ Stocks: §f" + shop.getAllItemsAmount());
            lore.add("§7■ Cliquer pour voir les stocks de ce shop");
            itemMeta.setLore(lore);
        }).setNextMenu(new ShopStocksMenu(getOwner(), shop, itemIndex)));

        content.put(8, new ItemBuilder(this, Material.LIME_WOOL, itemMeta -> {
            itemMeta.setDisplayName("§aCe shop vous appartient");
            if (shop.getOwner().isCompany()) {
                if (shop.getOwner().getCompany().getOwner().isCity()) {
                    itemMeta.setLore(List.of(
                            "§7■ Car vous faites partie de la team possédant l'entreprise"
                    ));
                }
            }
        }));

        content.put(36, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
            itemMeta.setDisplayName("§7Comment utiliser les shops");
        }).setOnClick(inventoryClickEvent -> {

            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            if (meta != null) {
                meta.setTitle("Guide des Shop");
                meta.setAuthor("Nocolm");
                meta.addPage(
                        "Comment utiliser les shops !\n\n" +
                                "§l§6Stock§r :\n" +
                                "1. Utilisez la commande §d§l/shop sell §r§7<prix> §r en tenant l'item en main\n" +
                                "2. Ajoutez les items dans le barril §c§l* le raccourci avec les chiffres ne fonctionnera pas *\n"
                );
                meta.addPage(
                        "3. Ouvrez une fois le shop pour renouveler son stock\n\n" +
                                "Et voilà comment utiliser votre shops"
                );

                book.setItemMeta(meta);
            }
            getOwner().closeInventory();
            getOwner().openBook(book);

            content.remove(44);
        }));
    }

    private ShopItem getCurrentItem() {
        if (itemIndex < 0 || itemIndex >= items.size()) {
            return null;
        }
        return items.get(itemIndex);
    }

    private boolean onFirstItem() {
        return itemIndex == 0;
    }

    private boolean onLastItem() {
        return itemIndex == items.size() - 1;
    }

    private void buyAccept() {
        MethodState buyState = shop.buy(getCurrentItem(), amountToBuy, getOwner());
        if (buyState == MethodState.ERROR) {
            getOwner().sendMessage("§cVous n'avez pas assez d'argent pour acheter cet item");
            getOwner().closeInventory();
            return;
        }
        if (buyState == MethodState.FAILURE) {
            getOwner().sendMessage("§cVous ne pouvez pas acheter vos propres items");
            getOwner().closeInventory();
            return;
        }
        if (buyState == MethodState.WARNING) {
            getOwner().sendMessage("§cIl n'y a pas assez de stock pour acheter cet item");
            getOwner().closeInventory();
            return;
        }
        if (buyState == MethodState.SPECIAL) {
            getOwner().sendMessage("§cVous n'avez pas assez de place dans votre inventaire");
            getOwner().closeInventory();
            return;
        }
        if (buyState == MethodState.ESCAPE) {
            getOwner().sendMessage("§cErreur lors de l'achat");
            getOwner().closeInventory();
            return;
        }
        getOwner().sendMessage("§aVous avez bien acheté " + amountToBuy + " " + ShopItem.getItemName(getCurrentItem().getItem()) + " pour " + (getCurrentItem().getPricePerItem() * amountToBuy) + "€");
        getOwner().closeInventory();
    }

    private void accept () {
        boolean isInCompany = companyManager.isInCompany(getOwner().getUniqueId());
        if (isInCompany) {
            MethodState deleteState = companyManager.getCompany(getOwner().getUniqueId()).deleteShop(getOwner(), shop.getUuid());
            if (deleteState == MethodState.ERROR) {
                getOwner().sendMessage("§cCe shop n'existe pas dans votre entreprise");
                return;
            }
            if (deleteState == MethodState.WARNING) {
                getOwner().sendMessage("§cCe shop n'est pas vide");
                return;
            }
            if (deleteState == MethodState.SPECIAL) {
                getOwner().sendMessage("§cIl vous faut au minimum le nombre d'argent remboursable pour supprimer un shop et obtenir un remboursement dans la banque de votre entreprise");
                return;
            }
            if (deleteState == MethodState.ESCAPE) {
                getOwner().sendMessage("§cCaisse introuvable (appelez un admin)");
            }
            getOwner().sendMessage("§a" + shop.getName() + " a été supprimé !");
            getOwner().sendMessage("§6[Shop]§a +75€ de remboursés sur la banque de l'entreprise");
        }
        else {
            MethodState methodState = playerShopManager.deleteShop(getOwner().getUniqueId());
            if (methodState == MethodState.WARNING) {
                getOwner().sendMessage("§cVotre shop n'est pas vide");
                return;
            }
            if (methodState == MethodState.ESCAPE) {
                getOwner().sendMessage("§cCaisse introuvable (appelez un admin)");
                return;
            }
            getOwner().sendMessage("§aVotre shop a bien été supprimé !");
            getOwner().sendMessage("§6[Shop]§a +400€ de remboursés sur votre compte personnel");
        }
        getOwner().closeInventory();
    }

    private void refuse() {
        getOwner().closeInventory();
    }
}

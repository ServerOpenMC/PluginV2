package fr.openmc.core.features.adminshop.menu.sell;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminShopSellConfirm extends Menu {
    private final BaseItems items;
    private final int quantity;
    private final String material;

    public AdminShopSellConfirm(Player player, BaseItems items, int quantity, String material) {
        super(player);
        this.items = items;
        this.quantity = quantity;
        this.material = material;
    }

    @Override
    public @NotNull String getName() {
        return "§6Confirmer la vente";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        for (int i = 0; i < getInventorySize().getSize(); i++) {
            content.put(i, new ItemBuilder(this, Material.BLACK_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName(" ")));
        }

        content.put(2, new ItemBuilder(this, Material.GREEN_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§aConfirmer la vente");
        }).setOnClick(event -> {
            if (!ItemUtils.hasEnoughItems(getOwner(), Objects.requireNonNull(Material.getMaterial(material == null ? items.named() : items.named() + "_" + material)), quantity)) {
                getOwner().sendMessage("§cVous n'avez pas assez d'items dans votre inventaire !");
                return;
            }

            EconomyManager economy = EconomyManager.getInstance();
            double totalAmount = items.getSellPrize() * quantity;


            ItemUtils.removeItemsFromInventory(getOwner(), Objects.requireNonNull(Material.getMaterial(material == null ? items.named() : items.named() + "_" + material)), quantity);
            economy.addBalance(getOwner().getUniqueId(), totalAmount);
            getOwner().sendMessage("§aVente confirmée !");
            getOwner().sendMessage("  §4- §c" + quantity + " " + items.getName() + " §7pour §a" + String.format("%.2f", totalAmount) + "$");

            getOwner().closeInventory();
        }));

        content.put(4, new ItemBuilder(this, Objects.requireNonNull(Material.getMaterial(material == null ? items.named() : items.named() + "_" + material)), itemMeta -> {
            itemMeta.setDisplayName(items.getName());
            double prizes = 0;
            if(items.getType() == ShopType.SELL_BUY) prizes = (items.getBuyPrize() / 2);
            else prizes = items.getBuyPrize();
            double finalPrize = prizes * quantity;
            itemMeta.setLore(Arrays.asList(
                    "§7Quantité: §e" + quantity,
                    "§7Prix total: §e" + String.format("%.2f", finalPrize) + "$"
            ));
        }));

        content.put(6, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§cAnnuler");
        }).setOnClick(event -> {
            getOwner().closeInventory();
        }));

        return content;
    }
}
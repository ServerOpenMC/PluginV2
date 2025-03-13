package fr.openmc.core.features.adminshop.menu.buy;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.Transaction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AdminShopBuyConfirm extends Menu {
    private final BaseItems items;
    private final int quantity;
    private final Material material;

    public AdminShopBuyConfirm(Player player, BaseItems items, int quantity, String materialName) {
        super(player);
        this.items = items;
        this.quantity = quantity;
        this.material = Material.getMaterial((materialName == null) ? items.named() : (items.named() + "_" + materialName));
    }

    @Override
    public @NotNull String getName() {
        return "§6Confirmer l'achat";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        ItemStack filler = new ItemBuilder(this, Material.BLACK_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName(" "));
        for (int i = 0; i < getInventorySize().getSize(); i++) {
            content.put(i, filler);
        }

        if (material == null) {
            getOwner().sendMessage("§cErreur: Matériau invalide.");
            getOwner().closeInventory();
            return content;
        }

        content.put(2, new ItemBuilder(this, Material.GREEN_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName("§aConfirmer"))
                .setOnClick(event -> confirmPurchase()));

        content.put(4, new ItemBuilder(this, material, itemMeta -> {
            itemMeta.setDisplayName(items.getName());
            double finalPrice = items.getPrize() * quantity;
            itemMeta.setLore(Arrays.asList(
                    "§7Quantité: §e" + quantity,
                    "§7Prix total: §e" + String.format("%.2f", finalPrice) + "$"
            ));
        }));

        content.put(6, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName("§cAnnuler"))
                .setBackButton());

        return content;
    }

    private void confirmPurchase() {
        Player player = getOwner();
        EconomyManager economy = EconomyManager.getInstance();
        double totalPrice = items.getPrize() * quantity;

        if (!hasEnoughSpace(player, material, quantity)) {
            player.sendMessage("§cVous n'avez pas assez d'espace dans votre inventaire !");
            player.closeInventory();
            return;
        }

        if (economy.getBalance(player.getUniqueId()) < totalPrice) {
            player.sendMessage("§cVous n'avez pas assez d'argent pour acheter cet item.");
            return;
        }

        economy.withdrawBalance(player.getUniqueId(), totalPrice);

        new Transaction("CONSOLE", player.getUniqueId().toString(), totalPrice, "Achat adminshop");

        distributeItems(player, material, quantity);

        player.sendMessage("§aAchat confirmé !");
        player.sendMessage("  §2+ §a" + quantity + " " + items.getName() + " §7pour §a" + String.format("%.2f", totalPrice) + "$");

        player.closeInventory();
    }

    private boolean hasEnoughSpace(Player player, Material item, int amount) {
        int freeSlots = 0;
        for (ItemStack is : player.getInventory().getStorageContents()) {
            if (is == null || is.getType() == Material.AIR) {
                freeSlots += item.getMaxStackSize();
            } else if (is.getType() == item && is.getAmount() < is.getMaxStackSize()) {
                freeSlots += (is.getMaxStackSize() - is.getAmount());
            }
            if (freeSlots >= amount) return true;
        }
        return false;
    }

    private void distributeItems(Player player, Material material, int totalQuantity) {
        int maxStackSize = material.getMaxStackSize();
        while (totalQuantity > 0) {
            int stackSize = Math.min(totalQuantity, maxStackSize);
            player.getInventory().addItem(new ItemStack(material, stackSize));
            totalQuantity -= stackSize;
        }
    }
}

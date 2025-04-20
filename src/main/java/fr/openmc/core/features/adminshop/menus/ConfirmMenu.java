package fr.openmc.core.features.adminshop.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfirmMenu extends Menu {

    private final AdminShopManager shopManager;
    private final ShopItem shopItem;
    private final boolean isBuying;
    private int quantity;
    private final Menu previousMenu;
    private final int maxQuantity;

    public ConfirmMenu(Player owner, AdminShopManager shopManager,
                       ShopItem shopItem, boolean isBuying, Menu previousMenu) {
        super(owner);
        this.shopManager = shopManager;
        this.shopItem = shopItem;
        this.previousMenu = previousMenu;
        this.isBuying = isBuying;
        this.quantity = 1;
        if (isBuying) this.maxQuantity = 64 * 36;
        else this.maxQuantity = countPlayerItems(owner, shopItem.getMaterial());
    }

    @Override
    public @NotNull String getName() {
        return "§f" + PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_adminshop%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();
        double pricePerUnit = isBuying ? shopItem.getActualBuyPrice() : shopItem.getActualSellPrice();
        double totalPrice = pricePerUnit * quantity;

        List<Component> lore = List.of(
                Component.text("§eQuantité: §f" + quantity),
                Component.text("§ePrix unitaire: §e" + shopManager.priceFormat.format(pricePerUnit)),
                Component.text("§ePrix total: §e" + shopManager.priceFormat.format(totalPrice))
        );

        content.put(9, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), meta -> {
            meta.displayName(Component.text("§cAnnuler"));
        }).setNextMenu(previousMenu));

        int[] quantitySteps = {-64, -10, -1, +1, +10, +64};
        int[] slots = {10, 11, 12, 14, 15, 16};

        for (int i = 0; i < quantitySteps.length; i++) {
            int step = quantitySteps[i];
            int slot = slots[i];
            content.put(slot, createQuantityButton(
                    (step > 0 ? "+" : "") + step,
                    CustomItemRegistry.getByName("omc_menus:" + Math.abs(step) + "_btn").getBest(),
                    e -> {
                        quantity = Math.max(1, Math.min(maxQuantity, quantity + step));
                        update();
                    }));
        }

        content.put(13, new ItemBuilder(this, shopItem.getMaterial(), meta -> {
            meta.displayName(Component.text("§f" + shopItem.getName()));
            meta.lore(lore);
        }));

        content.put(17, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), meta -> {
            meta.displayName(Component.text("§aAccepter"));
        }).setOnClick(event -> {
            getOwner().closeInventory();
            if (isBuying) shopManager.buyItem(getOwner(), shopItem.getId(), quantity);
            else shopManager.sellItem(getOwner(), shopItem.getId(), quantity);
        }));

        return content;
    }

    private void update() {
        this.open();
    }

    private ItemStack createQuantityButton(String text, ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        return new ItemBuilder(this, itemStack, meta ->
            meta.displayName(Component.text((text.contains("+") ? "§aAjouter " : "§cRetirer ") + text.replace("+", "").replace("-", ""))))
            .setItemId("quantity_" + text.replace("+", "plus").replace("-", "minus"))
            .setOnClick(action);
    }

    private int countPlayerItems(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents())
            if (item != null && item.getType() == material)
                count += item.getAmount();
        return count;
    }
}

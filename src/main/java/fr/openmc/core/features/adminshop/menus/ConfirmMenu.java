package fr.openmc.core.features.adminshop.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.features.economy.EconomyManager;
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
        int quantityToStack = Math.max(0, quantity / 64);

        List<Component> lore = List.of(
                Component.text("§8■ §eQuantité: §f" + quantity + " §7(§f" + quantityToStack + "§7 stack" + (quantityToStack > 1 ? "s" : "") + ")"),
                Component.text("§8■ §ePrix unitaire: §a" + shopManager.priceFormat.format(pricePerUnit) + EconomyManager.getEconomyIcon()),
                Component.text("§8■ §ePrix total: §a" + shopManager.priceFormat.format(totalPrice) + EconomyManager.getEconomyIcon())
        );

        content.put(9, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), meta -> {
            meta.displayName(Component.text("§cAnnuler"));
        }).setNextMenu(previousMenu));

        content.put(10, createQuantityButton("-64", CustomItemRegistry.getByName("omc_menus:64_btn").getBest(), event -> {
            if (quantity > 64) quantity -= 64;
            else quantity = 1;
            update();
        }));

        content.put(11, createQuantityButton("-10", CustomItemRegistry.getByName("omc_menus:minus_btn").getBest(), event -> {
            if (quantity > 10) quantity -= 10;
            update();
        }));

        content.put(12, createQuantityButton("-1", CustomItemRegistry.getByName("omc_menus:1_btn").getBest(), event -> {
            if (quantity > 1) quantity--;
            update();
        }));

        content.put(13, new ItemBuilder(this, shopItem.getMaterial(), meta -> {
            meta.displayName(Component.text("§f" + shopItem.getName()));
            meta.lore(lore);
        }));

        content.put(14, createQuantityButton("+1", CustomItemRegistry.getByName("omc_menus:1_btn").getBest(), event -> {
            if (!isBuying && shopManager.hasEnoughItems(getOwner(), shopItem.getMaterial(), quantity + 1)) {
                quantity = Math.min(maxQuantity, countPlayerItems(getOwner(), shopItem.getMaterial()));
            } else if (quantity < maxQuantity) {
                quantity++;
            }
            update();
        }));

        content.put(15, createQuantityButton("+10", CustomItemRegistry.getByName("omc_menus:plus_btn").getBest(), event -> {
            if (!isBuying && shopManager.hasEnoughItems(getOwner(), shopItem.getMaterial(), quantity + 10)) {
                quantity = Math.min(maxQuantity, countPlayerItems(getOwner(), shopItem.getMaterial()));
            } else if (quantity < maxQuantity) {
                quantity += 10;
            }
            update();
        }));

        content.put(16, createQuantityButton("+64", CustomItemRegistry.getByName("omc_menus:64_btn").getBest(), event -> {
            if (!isBuying && shopManager.hasEnoughItems(getOwner(), shopItem.getMaterial(), quantity + 64)) {
                quantity = Math.min(maxQuantity, countPlayerItems(getOwner(), shopItem.getMaterial()));
            } else if (quantity < maxQuantity) {
                quantity += 64;
            }
            update();
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

    /**
     * Creates a quantity button with the specified text and item stack.
     *
     * @param text      The text to display on the button.
     * @param itemStack The item stack to use for the button.
     * @param action    The action to perform when the button is clicked.
     * @return The created item stack.
     */
    private ItemStack createQuantityButton(String text, ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        return new ItemBuilder(this, itemStack, meta ->
            meta.displayName(Component.text((text.contains("+") ? "§aAjouter " : "§cRetirer ") + text.replace("+", "").replace("-", ""))))
            .setItemId("quantity_" + text.replace("+", "plus").replace("-", "minus"))
            .setOnClick(action);
    }

    /**
     * Counts the number of items of a specific material in a player's inventory.
     *
     * @param player   The player whose inventory to check.
     * @param material The material to count.
     * @return The count of items of the specified material in the player's inventory.
     */
    private int countPlayerItems(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents())
            if (item != null && item.getType() == material)
                count += item.getAmount();
        return count;
    }
}

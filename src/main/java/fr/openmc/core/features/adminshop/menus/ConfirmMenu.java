package fr.openmc.core.features.adminshop.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfirmMenu extends Menu {

    private final ShopItem shopItem;
    private final boolean isBuying;
    private int quantity;
    private final int maxQuantity;

    public ConfirmMenu(Player owner, ShopItem shopItem, boolean isBuying) {
        super(owner);
        this.shopItem = shopItem;
        this.isBuying = isBuying;
        this.quantity = 1;
        this.maxQuantity = isBuying ? ItemUtils.getFreePlacesForItem(owner, shopItem.getMaterial()) : countPlayerItems(owner, shopItem.getMaterial());
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.confirm.name");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-11::adminshop:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();
        double pricePerUnit = isBuying ? shopItem.getActualBuyPrice() : shopItem.getActualSellPrice();
        double totalPrice = pricePerUnit * quantity;
        int quantityToStack = Math.max(0, quantity / 64);

        List<Component> lore = TranslationManager.translationLore("feature.adminshop.menu.confirm.lore",
                Component.text(quantity), Component.text(quantityToStack), (quantityToStack > 1 ? Component.text("s") : Component.empty()),
                Component.text(AdminShopManager.priceFormat.format(pricePerUnit)), Component.text(EconomyManager.getEconomyIcon()),
                Component.text(AdminShopManager.priceFormat.format(totalPrice)), Component.text(EconomyManager.getEconomyIcon())
        );

        content.put(9, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), meta -> {
            meta.displayName(TranslationManager.translation("messages.global.cancel"));
        }, true));

        content.put(10, createQuantityButton("-64", CustomItemRegistry.getByName("omc_menus:64_btn").getBest(), event -> {
            if (quantity > 64) quantity -= 64;
            else quantity = 1;
            this.open();
        }));

        content.put(11, createQuantityButton("-10", CustomItemRegistry.getByName("omc_menus:minus_btn").getBest(), event -> {
            if (quantity > 10) quantity -= 10;
            else quantity = 1;
            this.open();
        }));

        content.put(12, createQuantityButton("-1", CustomItemRegistry.getByName("omc_menus:1_btn").getBest(), event -> {
            if (quantity > 1) quantity--;
            else quantity = 1;
            this.open();
        }));

        content.put(13, new ItemBuilder(this, shopItem.getMaterial(), meta -> {
            meta.displayName(shopItem.getName().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(event -> {
            if (event.getClick().equals(ClickType.MIDDLE)) {
                DialogInput.sendFloat(
                        getOwner(),
                        TranslationManager.translation("feature.adminshop.menu.confirm.input"),
                        1,
                        maxQuantity,
                        quantity,
                        input -> {
                            if (input != null) {
                                int inputQuantity = (int) input.doubleValue();
                                if (inputQuantity > 0) {
                                    quantity = Math.min(inputQuantity, maxQuantity);
                                    this.open();
                                }
                            }
                        }
                );
            }
        }));

        content.put(14, createQuantityButton("+1", CustomItemRegistry.getByName("omc_menus:1_btn").getBest(), event -> increaseQuantity(1)));

        content.put(15, createQuantityButton("+10", CustomItemRegistry.getByName("omc_menus:plus_btn").getBest(), event -> increaseQuantity(10)));

        content.put(16, createQuantityButton("+64", CustomItemRegistry.getByName("omc_menus:64_btn").getBest(), event -> increaseQuantity(64)));

        content.put(17, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), meta -> {
            meta.displayName(TranslationManager.translation("messages.global.accept"));
        }).setOnClick(event -> {
            getOwner().closeInventory();
            if (isBuying) AdminShopManager.buyItem(getOwner(), shopItem.getId(), quantity);
            else AdminShopManager.sellItem(getOwner(), shopItem.getId(), quantity);
        }));

        return content;
    }

    /**
     * Creates a quantity button with the specified text and item stack.
     *
     * @param text      The text to display on the button.
     * @param itemStack The item stack to use for the button.
     * @param action    The action to perform when the button is clicked.
     * @return The created item stack.
     */
    private ItemBuilder createQuantityButton(String text, ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        boolean plus = text.contains("+");
        return new ItemBuilder(this, itemStack, meta ->
            meta.displayName(TranslationManager.translation("feature.adminshop.menu.confirm.quantity",
                    plus ?
                            TranslationManager.translation("feature.adminshop.menu.confirm.add") :
                    TranslationManager.translation("feature.adminshop.menu.confirm.remove"),
                    Component.text(text.replace("+", "").replace("-", "")))
                    .color(plus ? NamedTextColor.GREEN : NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false)))
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

    /**
     * Increases the quantity by the specified amount, ensuring it does not exceed the maximum allowed quantity.
     *
     * @param amount The amount to increase the quantity by.
     */
    private void increaseQuantity(int amount) {
        if (!isBuying) {
            int playerItemCount = countPlayerItems(getOwner(), shopItem.getMaterial());
            quantity = Math.min(quantity + amount, playerItemCount);
        } else {
            quantity = Math.min(quantity + amount, maxQuantity);
        }
        this.open();
    }


    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}

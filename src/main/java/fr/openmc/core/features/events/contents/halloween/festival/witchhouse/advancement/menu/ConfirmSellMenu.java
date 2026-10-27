package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.events.contents.halloween.festival.shop.ShopMoney;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

public class ConfirmSellMenu extends Menu {

    // Copy of the ConfirmMenu from the AdminShop //

    private final ShopMoney shopItem;
    private int quantity;
    private final int maxQuantity;

    public ConfirmSellMenu(Player owner, ShopMoney shopItem) {
        super(owner);
        this.shopItem = shopItem;
        this.quantity = 1;
        this.maxQuantity = countPlayerCandies();
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.confirm.name");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::halloween_purchase_confirm_menu:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();
        double scorePerUnit = shopItem.getScores();
        double totalScore = scorePerUnit * quantity;
        int quantityToStack = Math.max(0, quantity / 64);

        Component shiftRightSellAll = TranslationManager.translation("feature.adminshop.menu.confirm.sell_all");

        List<Component> lore = TranslationManager.translationLore("feature.adminshop.menu.confirm.lore",
                Component.text(String.valueOf(quantity), NamedTextColor.WHITE),
                Component.text(String.valueOf(quantityToStack), NamedTextColor.WHITE),
                quantityToStack > 1 ? Component.text("s", NamedTextColor.WHITE) : Component.empty(),
                Component.text(AdminShopManager.priceFormat.format(scorePerUnit), NamedTextColor.GREEN),
                Component.text(WitchShopManager.getHalloweenPointIcon(), NamedTextColor.GOLD),
                Component.text(AdminShopManager.priceFormat.format(totalScore), NamedTextColor.GREEN),
                Component.text(WitchShopManager.getHalloweenPointIcon(), NamedTextColor.GOLD),
                shiftRightSellAll

        );

        content.put(9, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.REFUSE_BTN, true));

        content.put(10, createQuantityButton("-64", OMCRegistry.CUSTOM_ITEMS.BTN_64, event -> {
            if (quantity > 64) quantity -= 64;
            else quantity = 1;
            this.open();
        }));

        content.put(11, createQuantityButton("-10", OMCRegistry.CUSTOM_ITEMS.MINUS_BTN, event -> {
            if (quantity > 10) quantity -= 10;
            else quantity = 1;
            this.open();
        }));

        content.put(12, createQuantityButton("-1", OMCRegistry.CUSTOM_ITEMS.BTN_1, event -> {
            if (quantity > 1) quantity--;
            else quantity = 1;
            this.open();
        }));

        content.put(13, new ItemMenuBuilder(this, shopItem.getItem(), meta -> {
            meta.displayName(shopItem.getName().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(event -> {
            switch (event.getClick()) {
                case ClickType.MIDDLE -> DialogInput.sendFloat(
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
                case ClickType.SHIFT_RIGHT -> {
                    int itemCount = ItemUtils.countItems(getOwner(), shopItem.getItem().getBest());
                    if (itemCount > 0) {
                        getOwner().closeInventory();
                        WitchShopManager.sellItem(
                                getOwner(),
                                shopItem,
                                itemCount
                        );
                    }
                }
            }
        }));

        content.put(14, createQuantityButton("+1", OMCRegistry.CUSTOM_ITEMS.BTN_1, event -> increaseQuantity(1)));

        content.put(15, createQuantityButton("+10", OMCRegistry.CUSTOM_ITEMS.BTN_10, event -> increaseQuantity(10)));

        content.put(16, createQuantityButton("+64", OMCRegistry.CUSTOM_ITEMS.BTN_64, event -> increaseQuantity(64)));

        content.put(17, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ACCEPT_BTN, meta -> {
            meta.displayName(TranslationManager.translation("messages.global.accept"));
        }).setOnClick(event -> {
            getOwner().closeInventory();
            WitchShopManager.sellItem(getOwner(), shopItem, quantity);
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
    private ItemMenuBuilder createQuantityButton(String text, ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        boolean plus = text.contains("+");
        return new ItemMenuBuilder(this, itemStack, meta ->
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
     * Creates a quantity button with the specified text and item stack.
     *
     * @param text      The text to display on the button.
     * @param customItem The CustomItem to use for the button.
     * @param action    The action to perform when the button is clicked.
     * @return The created item stack.
     */
    private ItemMenuBuilder createQuantityButton(String text, CustomItem customItem, Consumer<InventoryClickEvent> action) {
        return this.createQuantityButton(text, customItem.getBest(), action);
    }

    /**
     * Counts the number of items of a specific material in a player's inventory.
     *
     * @return The count of items of the specified material in the player's inventory.
     */
    private int countPlayerCandies() {
        int amount = 0;

        for (ItemStack item : getOwner().getInventory().getContents()) {
            if (item == null) continue;

            CustomStack customStack = CustomStack.byItemStack(item);
            if (customStack == null) continue;

            if (customStack.getNamespacedID().equals(
                    shopItem.getItem().getCustomStack().getNamespacedID()))
                amount += item.getAmount();
        }

        return amount;
    }

    /**
     * Increases the quantity by the specified amount, ensuring it does not exceed the maximum allowed quantity.
     *
     * @param amount The amount to increase the quantity by.
     */
    private void increaseQuantity(int amount) {
        int playerItemCount = countPlayerCandies();
        quantity = Math.min(quantity + amount, playerItemCount);
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

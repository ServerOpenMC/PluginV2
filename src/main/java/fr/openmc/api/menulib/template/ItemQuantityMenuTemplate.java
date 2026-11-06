package fr.openmc.api.menulib.template;

import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
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

public abstract class ItemQuantityMenuTemplate extends Menu {

    protected int quantity = 1;
    protected final int maxQuantity;

    private final Component name;
    private final String texture;

    public ItemQuantityMenuTemplate(Player owner, int maxQuantity, Component name, String texture) {
        super(owner);
        this.maxQuantity = Math.max(1, maxQuantity);
        this.texture = texture;
        this.name = name;
    }

    @Override
    public @NotNull Component getName() {
        return name;
    }

    @Override
    public String getTexture() {
        return texture;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    protected abstract ItemStack getDisplayItem();
    protected abstract Component getDisplayName();
    protected abstract List<Component> getDisplayLore();

    protected abstract void onAccept();

    protected void onDeny() {
        getOwner().closeInventory();
    }

    protected void onSpecialClick(ClickType clickType) {}

    protected void addAdditionalContent(Map<Integer, ItemMenuBuilder> content) {}

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        content.put(9, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.REFUSE_BTN, true)
                .setOnClick(event -> onDeny()));

        content.put(10, createQuantityButton("-64", OMCRegistry.CUSTOM_ITEMS.BTN_64.getBest(),
                () -> decreaseQuantity(64)));

        content.put(11, createQuantityButton("-10", OMCRegistry.CUSTOM_ITEMS.MINUS_BTN.getBest(),
                () -> decreaseQuantity(10)));

        content.put(12, createQuantityButton("-1", OMCRegistry.CUSTOM_ITEMS.BTN_1.getBest(),
                () -> decreaseQuantity(1)));

        content.put(13, new ItemMenuBuilder(this, getDisplayItem(), meta -> {
            meta.displayName(getDisplayName());
            meta.lore(getDisplayLore());
        }).setOnClick(event -> {
            if (event.getClick() == ClickType.MIDDLE) {
                DialogInput.sendFloat(
                        getOwner(),
                        TranslationManager.translation("api.menulib.menu.quantity_confirm.input"),
                        1,
                        maxQuantity,
                        quantity,
                        input -> {
                            if (input != null) {
                                int inputQuantity = (int) input.doubleValue();
                                if (inputQuantity > 0) {
                                    quantity = Math.min(inputQuantity, maxQuantity);
                                    open();
                                }
                            }
                        }
                );
            } else {
                onSpecialClick(event.getClick());
            }
        }));

        content.put(14, createQuantityButton("+1", OMCRegistry.CUSTOM_ITEMS.BTN_1.getBest(),
                () -> increaseQuantity(1)));

        content.put(15, createQuantityButton("+10", OMCRegistry.CUSTOM_ITEMS.BTN_10.getBest(),
                () -> increaseQuantity(10)));

        content.put(16, createQuantityButton("+64", OMCRegistry.CUSTOM_ITEMS.BTN_64.getBest(),
                () -> increaseQuantity(64)));

        content.put(17, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ACCEPT_BTN, meta ->
                meta.displayName(TranslationManager.translation("messages.global.accept"))
        ).setOnClick(_ -> onAccept()));

        addAdditionalContent(content);

        return content;
    }

    private ItemMenuBuilder createQuantityButton(String text, ItemStack itemStack, Runnable action) {
        boolean plus = text.startsWith("+");

        return new ItemMenuBuilder(this, itemStack, meta -> {
            Component name = plus
                    ? TranslationManager.translation("api.menulib.menu.quantity_confirm.add")
                    : TranslationManager.translation("api.menulib.menu.quantity_confirm.remove");


            meta.displayName(
                    name.append(Component.text(
                                            text.replace("+", "").replace("-", ""))
                            .color(plus ? NamedTextColor.GREEN : NamedTextColor.RED)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        }).setItemId("quantity_" + text.replace("+", "plus").replace("-", "minus"))
                .setOnClick(_ -> action.run());
    }

    private void decreaseQuantity(int amount) {
        quantity = Math.max(1, quantity - amount);
        open();
    }

    private void increaseQuantity(int amount) {
        quantity = Math.min(maxQuantity, quantity + amount);
        open();
    }
}

package fr.openmc.core.features.adminshop.menus.confirm;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.template.ItemQuantityMenuTemplate;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfirmSellMenu extends ItemQuantityMenuTemplate {

    private final ShopItem shopItem;
    private final Runnable onFinish;

    public ConfirmSellMenu(Player owner, ShopItem shopItem, Runnable onFinish) {
        super(
                owner,
                countPlayerItems(owner, shopItem),
                TranslationManager.translation("feature.adminshop.menu.confirm.name"),
                FontImageWrapper.replaceFontImages("§r§f:offset_-11::adminshop:")
        );

        this.shopItem = shopItem;
        this.onFinish = onFinish;
    }

    @Override
    protected ItemStack getDisplayItem() {
        return ItemStack.of(shopItem.getMaterial());
    }

    @Override
    protected Component getDisplayName() {
        return shopItem.getName()
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    protected List<Component> getDisplayLore() {
        double pricePerUnit = shopItem.getActualSellPrice();
        double totalPrice = pricePerUnit * quantity;

        int quantityToStack = quantity / 64;

        return TranslationManager.translationLore(
                "feature.adminshop.menu.confirm.lore",
                Component.text(String.valueOf(quantity), NamedTextColor.WHITE),
                Component.text(String.valueOf(quantityToStack), NamedTextColor.WHITE),
                quantityToStack > 1 ? Component.text("s", NamedTextColor.WHITE) : Component.empty(),
                Component.text(AdminShopManager.priceFormat.format(pricePerUnit), NamedTextColor.GREEN),
                Component.text(EconomyManager.getEconomyIcon(), NamedTextColor.GREEN),
                Component.text(AdminShopManager.priceFormat.format(totalPrice), NamedTextColor.GREEN),
                Component.text(EconomyManager.getEconomyIcon(), NamedTextColor.GREEN),
                TranslationManager.translation("feature.adminshop.menu.confirm.sell_all")
        );
    }

    @Override
    protected void onAccept() {
        getOwner().closeInventory();
        AdminShopManager.sellItem(getOwner(), shopItem.getId(), quantity);
        if (onFinish != null) onFinish.run();
    }

    @Override
    protected void onSpecialClick(ClickType clickType) {
        if (clickType != ClickType.SHIFT_RIGHT) return;

        int itemCount = countPlayerItems(getOwner(), shopItem);

        if (itemCount <= 0) return;

        getOwner().closeInventory();
        AdminShopManager.sellItem(getOwner(), shopItem.getId(), itemCount);
    }

    private static int countPlayerItems(Player player, ShopItem shopItem) {
        return ItemUtils.countItems(
                player,
                ItemStack.of(shopItem.getMaterial())
        );
    }
}


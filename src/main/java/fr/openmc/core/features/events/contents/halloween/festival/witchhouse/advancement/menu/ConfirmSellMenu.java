package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.template.ItemQuantityMenuTemplate;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.events.contents.halloween.festival.shop.ShopMoney;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
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

    private final ShopMoney shopItem;

    public ConfirmSellMenu(Player owner, ShopMoney shopItem) {
        super(owner, ItemUtils.countItems(
                owner,
                shopItem.getItem().getBest()),
                TranslationManager.translation("feature.adminshop.menu.confirm.name"),
                FontImageWrapper.replaceFontImages("§r§f:offset_-48::halloween_purchase_confirm_menu:")
        );

        this.shopItem = shopItem;
    }

    @Override
    protected ItemStack getDisplayItem() {
        return shopItem.getItem().getBest();
    }

    @Override
    protected Component getDisplayName() {
        return shopItem.getName()
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    protected List<Component> getDisplayLore() {
        double scorePerUnit = shopItem.getScores();
        double totalScore = scorePerUnit * quantity;

        // todo: ede
        int quantityToStack = quantity / 64;

        return TranslationManager.translationLore(
                "feature.adminshop.menu.confirm.lore",
                Component.text(String.valueOf(quantity), NamedTextColor.WHITE),
                Component.text(String.valueOf(quantityToStack), NamedTextColor.WHITE),
                quantityToStack > 1 ? Component.text("s", NamedTextColor.WHITE) : Component.empty(),
                Component.text(AdminShopManager.priceFormat.format(scorePerUnit), NamedTextColor.GREEN),
                Component.text(WitchShopManager.getHalloweenPointIcon(), NamedTextColor.GOLD),
                Component.text(AdminShopManager.priceFormat.format(totalScore), NamedTextColor.GREEN),
                Component.text(WitchShopManager.getHalloweenPointIcon(), NamedTextColor.GOLD),
                TranslationManager.translation("feature.adminshop.menu.confirm.sell_all")
        );
    }

    @Override
    protected void onAccept() {
        getOwner().closeInventory();
        WitchShopManager.sellItem(getOwner(), shopItem, quantity);
    }

    @Override
    protected void onSpecialClick(ClickType clickType) {
        if (clickType != ClickType.SHIFT_RIGHT) return;

        int itemCount = ItemUtils.countItems(
                getOwner(),
                shopItem.getItem().getBest()
        );

        if (itemCount > 0) {
            getOwner().closeInventory();
            WitchShopManager.sellItem(getOwner(), shopItem, itemCount);
        }
    }
}


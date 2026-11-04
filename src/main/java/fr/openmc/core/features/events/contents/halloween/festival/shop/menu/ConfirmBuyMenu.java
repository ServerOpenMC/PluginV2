package fr.openmc.core.features.events.contents.halloween.festival.shop.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.template.ItemQuantityMenuTemplate;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopItem;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfirmBuyMenu extends ItemQuantityMenuTemplate {

    private final WitchShopItem shopItem;

    public ConfirmBuyMenu(Player owner, WitchShopItem shopItem) {
        super(owner,
                getMaxBuyQuantity(owner, shopItem),
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
        return shopItem.getName();
    }

    @Override
    protected List<Component> getDisplayLore() {
        int pricePerUnit = shopItem.getPrice();
        int totalPrice = pricePerUnit * quantity;

        int scorePerUnit = shopItem.getShopMoney().getScores();
        int totalScore = scorePerUnit * quantity;

        int quantityToStack = quantity / 64;

        return TranslationManager.translationLore(
                "feature.events.halloween.festival.shop.confirm_lore",
                Component.text(String.valueOf(quantity), NamedTextColor.WHITE),
                Component.text(String.valueOf(quantityToStack), NamedTextColor.WHITE),
                quantityToStack > 1 ? Component.text("s", NamedTextColor.WHITE) : Component.empty(),
                Component.text(AdminShopManager.priceFormat.format(pricePerUnit), NamedTextColor.GREEN),
                shopItem.getShopMoney().getItem().getSprite(),
                Component.text(AdminShopManager.priceFormat.format(scorePerUnit), NamedTextColor.GREEN),
                Component.text(WitchShopManager.getHalloweenPointIcon()),
                Component.text(AdminShopManager.priceFormat.format(totalPrice), NamedTextColor.GREEN),
                shopItem.getShopMoney().getItem().getSprite(),
                Component.text(AdminShopManager.priceFormat.format(totalScore), NamedTextColor.GREEN),
                Component.text(WitchShopManager.getHalloweenPointIcon())
        );
    }

    @Override
    protected void onAccept() {
        getOwner().closeInventory();
        WitchShopManager.buyItem(getOwner(), shopItem, quantity);
    }

    private static int getMaxBuyQuantity(Player player, WitchShopItem shopItem) {
        int freePlaces = ItemUtils.getFreePlacesForItem(
                player,
                shopItem.getItem().getBest()
        );

        double buyPrice = shopItem.getPrice();

        if (buyPrice <= 0) return freePlaces;

        double balance = ItemUtils.countItems(player, shopItem.getShopMoney().getItem().getBest());
        int affordable = (int) Math.floor(balance / buyPrice);

        return Math.min(freePlaces, affordable);
    }
}


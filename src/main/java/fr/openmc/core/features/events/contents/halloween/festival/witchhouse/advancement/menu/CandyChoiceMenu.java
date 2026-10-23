package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.shop.ShopMoney;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandyChoiceMenu extends Menu {

    public CandyChoiceMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.confirm.name");//TODO
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::halloween_deposit_menu:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        content.put(9, new ItemMenuBuilder(this, ShopMoney.MONSTER_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
            itemMeta.lore(getCandyPoints(ShopMoney.MONSTER_CANDY));
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.MONSTER_CANDY).open();
        }));

        content.put(20, new ItemMenuBuilder(this, ShopMoney.GREEN_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
            itemMeta.lore(getCandyPoints(ShopMoney.GREEN_CANDY));
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.GREEN_CANDY).open();
        }));

        content.put(13, new ItemMenuBuilder(this, ShopMoney.BLUE_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
            itemMeta.lore(getCandyPoints(ShopMoney.BLUE_CANDY));
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.BLUE_CANDY).open();
        }));

        content.put(24, new ItemMenuBuilder(this, ShopMoney.RED_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
            itemMeta.lore(getCandyPoints(ShopMoney.RED_CANDY));
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.RED_CANDY).open();
        }));

        content.put(17, new ItemMenuBuilder(this, ShopMoney.YELLOW_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
            itemMeta.lore(getCandyPoints(ShopMoney.YELLOW_CANDY));
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.YELLOW_CANDY).open();
        }));

        content.put(49, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_CANCEL, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.close"));
        }).setOnClick(event -> {
            new AdvancementMenu(getOwner()).open();
        }));

        return content;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private List<Component> getCandyPoints(ShopMoney money) {
        return List.of(Component.text("Valeur : " + money.getScores() + " " + WitchShopManager.getHalloweenPointIcon())
                .color(NamedTextColor.GOLD)
        );//TODO translate
    }
}

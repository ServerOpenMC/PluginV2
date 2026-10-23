package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.shop.ShopMoney;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
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
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::halloween_deposit_menu:"); //TODO
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
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.MONSTER_CANDY);
        }));

        content.put(20, new ItemMenuBuilder(this, ShopMoney.GREEN_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.GREEN_CANDY);
        }));

        content.put(13, new ItemMenuBuilder(this, ShopMoney.BLUE_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.BLUE_CANDY);
        }));

        content.put(24, new ItemMenuBuilder(this, ShopMoney.RED_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.RED_CANDY);
        }));

        content.put(17, new ItemMenuBuilder(this, ShopMoney.YELLOW_CANDY.getItem(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page")); //TODO
        }).setOnClick(click -> {
                new ConfirmSellMenu(getOwner(), ShopMoney.YELLOW_CANDY);
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
}

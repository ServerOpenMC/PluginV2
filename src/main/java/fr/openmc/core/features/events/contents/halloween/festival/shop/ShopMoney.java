package fr.openmc.core.features.events.contents.halloween.festival.shop;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum ShopMoney {
    RED_CANDY(TranslationManager.translation("itemsadder.omc_halloween.candy_red.name"), OMCRegistry.CUSTOM_ITEMS.CANDY_RED, 1000),
    YELLOW_CANDY(TranslationManager.translation("itemsadder.omc_halloween.candy_yellow.name"), OMCRegistry.CUSTOM_ITEMS.CANDY_YELLOW, 10000), //extremely rare no craft
    GREEN_CANDY(TranslationManager.translation("itemsadder.omc_halloween.candy_green.name"), OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN, 10),
    BLUE_CANDY(TranslationManager.translation("itemsadder.omc_halloween.candy_blue.name"), OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE, 100),
    MONSTER_CANDY(TranslationManager.translation("itemsadder.omc_halloween.candy_brown.name"), OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 1),
    ;

    private final Component name;
    private final CustomItem item;
    private final int scores;

    ShopMoney(Component name, CustomItem item, int scores) {
        this.name = name;
        this.item = item;
        this.scores = scores;
    }
}

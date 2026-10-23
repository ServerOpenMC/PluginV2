package fr.openmc.core.features.events.contents.halloween.festival.shop;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum ShopMoney {
    //TODO rajouter les crafts + Translation pour 'name'
    RED_CANDY(Component.text("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_RED, 1000),
    YELLOW_CANDY(Component.text("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_YELLOW, 10000), //extremely rare no craft
    GREEN_CANDY(Component.text("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN, 10),
    BLUE_CANDY(Component.text("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE, 100),
    MONSTER_CANDY(Component.text("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 1),
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

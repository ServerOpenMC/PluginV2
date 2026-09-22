package fr.openmc.core.features.events.contents.halloween.halloween.shop;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;

@Getter
public enum ShopMoney {
    //TODO rajouter les crafts des bonbons et les autre dans le shopMoney
    RED_CANDY(WitchShopManager.getMoneyItemIcon("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_RED),
    YELLOW_CANDY(WitchShopManager.getMoneyItemIcon("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_YELLOW),
    GREEN_CANDY(WitchShopManager.getMoneyItemIcon("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN),
    BLUE_CANDY(WitchShopManager.getMoneyItemIcon("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE),
    MONSTER_CANDY(WitchShopManager.getMoneyItemIcon("candy_brown"), OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN),
    ;

    private final String icon;
    private final CustomItem item;

    ShopMoney(String icon, CustomItem item) {
        this.icon = icon;
        this.item = item;
    }
}

package fr.openmc.core.features.events.contents.halloween.halloween.shop;

import lombok.Getter;

@Getter
public enum ShopMoney {
    //TODO rajouter les crafts des bonbons et les autre dans le shopMoney
    MONSTER_CANDY(WitchShopManager.getMoneyItemIcon("candy_brown")),
    ;

    private final String icon;

    ShopMoney(String icon) {
        this.icon = icon;
    }
}

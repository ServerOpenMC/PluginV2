package fr.openmc.core.features.events.contents.halloween.halloween.shop;

import lombok.Getter;

@Getter
public enum ShopMoney {
    MONSTER_CANDY(WitchShopManager.getMoneyItemIcon("")),
    ;

    private final String icon;

    ShopMoney(String icon) {
        this.icon = icon;
    }
}

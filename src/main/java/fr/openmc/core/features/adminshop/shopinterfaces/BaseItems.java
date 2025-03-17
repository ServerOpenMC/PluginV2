package fr.openmc.core.features.adminshop.shopinterfaces;

import fr.openmc.core.features.adminshop.menu.category.ShopType;

public interface BaseItems {
    String named();
    String getName();
    double getBuyPrize();
    double getSellPrize();
    int getSlots();
    ShopType getType();
    int getMaxStack();
}

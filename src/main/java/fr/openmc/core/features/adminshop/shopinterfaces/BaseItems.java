package fr.openmc.core.features.adminshop.shopinterfaces;

import fr.openmc.core.features.adminshop.menu.category.ShopType;

public interface BaseItems {
    String named();
    String getName();
    double getPrize();
    int getSlots();
    ShopType getType();
    int getMaxStack();
}

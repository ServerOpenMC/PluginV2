package fr.openmc.core.features.cosmetics.bodycostmetics;

import org.bukkit.inventory.ItemStack;

public interface BodyCosmetic {
    ItemStack getItem();

    String getName();

    int getPrice();
}

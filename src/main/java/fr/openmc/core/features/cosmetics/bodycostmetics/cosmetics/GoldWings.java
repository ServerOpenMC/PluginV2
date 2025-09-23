package fr.openmc.core.features.cosmetics.bodycostmetics.cosmetics;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.features.cosmetics.bodycostmetics.BodyCosmetic;
import org.bukkit.inventory.ItemStack;

public class GoldWings implements BodyCosmetic {

    private static final ItemStack ITEM_STACK = CustomStack.getInstance("omc_cosmetics:gold_wings").getItemStack();
    private static final String NAME = "Ailles dorées";
    private static final int PRICE = 1000;

    @Override
    public ItemStack getItem() {
        return ITEM_STACK;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getPrice() {
        return PRICE;
    }
}

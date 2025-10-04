package fr.openmc.core.features.cosmetics.bodycostmetics.cosmetics;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.features.cosmetics.bodycostmetics.BodyCosmetic;
import org.bukkit.inventory.ItemStack;

public class BlueBackPack implements BodyCosmetic {

    private static final ItemStack ITEM_STACK = CustomStack.getInstance("omc_cosmetics:blue_backpack").getItemStack();
    private static final String NAME = "Sac à dos bleu";
    private static final int PRICE = 100;

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

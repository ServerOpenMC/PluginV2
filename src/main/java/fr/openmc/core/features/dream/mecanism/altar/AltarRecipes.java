package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public enum AltarRecipes {

    SOUL_ORB(
            OMCRegistry.DREAM_ITEM.DOMINATION_ORB,
            OMCRegistry.DREAM_ITEM.SOUL_ORB,
            20
    ),
    SOUL_HELMET(
            OMCRegistry.DREAM_ITEM.OLD_CREAKING_HELMET,
            OMCRegistry.DREAM_ITEM.SOUL_HELMET,
            10
    ),
    SOUL_CHESTPLATE(
            OMCRegistry.DREAM_ITEM.OLD_CREAKING_CHESTPLATE,
            OMCRegistry.DREAM_ITEM.SOUL_CHESTPLATE,
            10
    ),
    SOUL_LEGGINGS(
            OMCRegistry.DREAM_ITEM.OLD_CREAKING_LEGGINGS,
            OMCRegistry.DREAM_ITEM.SOUL_LEGGINGS,
            10
    ),
    SOUL_BOOTS(
            OMCRegistry.DREAM_ITEM.OLD_CREAKING_BOOTS,
            OMCRegistry.DREAM_ITEM.SOUL_BOOTS,
            10
    ),
    SOUL_AXE(
            OMCRegistry.DREAM_ITEM.OLD_CREAKING_AXE,
            OMCRegistry.DREAM_ITEM.SOUL_AXE,
            15
    ),
    ;

    private final DreamItem input;
    private final DreamItem output;
    private final int soulsRequired;

    AltarRecipes(DreamItem input, DreamItem output, int soulsRequired) {
        this.input = input;
        this.output = output;
        this.soulsRequired = soulsRequired;
    }

    public static AltarRecipes match(ItemStack item) {
        for (AltarRecipes recipe : values()) {
            DreamItem dreamItem = OMCRegistry.DREAM_ITEM.getByItemStack(item);
            if (dreamItem == null) continue;

            if (dreamItem.equals(recipe.getInput())) {
                return recipe;
            }
        }
        return null;
    }

    public static AltarRecipes match(DreamItem dreamItem) {
        for (AltarRecipes recipe : values()) {
            if (dreamItem.equals(recipe.getInput())) {
                return recipe;
            }
        }
        return null;
    }
}

package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public enum AltarRecipes {

    SOUL_ORB(
            DreamItemRegistry.DOMINATION_ORB,
            DreamItemRegistry.SOUL_ORB,
            20
    ),
    SOUL_HELMET(
            DreamItemRegistry.OLD_CREAKING_HELMET,
            DreamItemRegistry.SOUL_HELMET,
            10
    ),
    SOUL_CHESTPLATE(
            DreamItemRegistry.OLD_CREAKING_CHESTPLATE,
            DreamItemRegistry.SOUL_CHESTPLATE,
            10
    ),
    SOUL_LEGGINGS(
            DreamItemRegistry.OLD_CREAKING_LEGGINGS,
            DreamItemRegistry.SOUL_LEGGINGS,
            10
    ),
    SOUL_BOOTS(
            DreamItemRegistry.OLD_CREAKING_BOOTS,
            DreamItemRegistry.SOUL_BOOTS,
            10
    ),
    SOUL_AXE(
            DreamItemRegistry.OLD_CREAKING_AXE,
            DreamItemRegistry.SOUL_AXE,
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
            DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
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

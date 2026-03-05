package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public enum AltarRecipes {

    SOUL_ORB(
            DreamItemRegistry.getByName("domination_orb"),
            DreamItemRegistry.getByName("ame_orb"),
            20
    ),
    SOUL_HELMET(
            DreamItemRegistry.getByName("old_creaking_helmet"),
            DreamItemRegistry.getByName("soul_helmet"),
            10
    ),
    SOUL_CHESTPLATE(
            DreamItemRegistry.getByName("old_creaking_chestplate"),
            DreamItemRegistry.getByName("soul_chestplate"),
            10
    ),
    SOUL_LEGGINGS(
            DreamItemRegistry.getByName("old_creaking_leggings"),
            DreamItemRegistry.getByName("soul_leggings"),
            10
    ),
    SOUL_BOOTS(
            DreamItemRegistry.getByName("old_creaking_boots"),
            DreamItemRegistry.getByName("soul_boots"),
            10
    ),
    SOUL_AXE(
            DreamItemRegistry.getByName("old_creaking_axe"),
            DreamItemRegistry.getByName("soul_axe"),
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

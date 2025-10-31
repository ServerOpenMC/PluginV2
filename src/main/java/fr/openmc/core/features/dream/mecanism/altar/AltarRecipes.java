package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.features.dream.models.registry.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public enum AltarRecipes {

    SOUL_ORB(
            DreamItemRegistry.getByName("omc_dream:domination_orb"),
            20,
            DreamItemRegistry.getByName("omc_dream:ame_orb")
    ),
    SOUL_HELMET(
            DreamItemRegistry.getByName("omc_dream:old_creaking_helmet"),
            10,
            DreamItemRegistry.getByName("omc_dream:soul_helmet")
    ),
    SOUL_CHESTPLATE(
            DreamItemRegistry.getByName("omc_dream:old_creaking_chesplate"),
            10,
            DreamItemRegistry.getByName("omc_dream:soul_chesplate")
    ),
    SOUL_LEGGINGS(
            DreamItemRegistry.getByName("omc_dream:old_creaking_leggings"),
            10,
            DreamItemRegistry.getByName("omc_dream:soul_leggings")
    ),
    SOUL_BOOTS(
            DreamItemRegistry.getByName("omc_dream:old_creaking_boots"),
            10,
            DreamItemRegistry.getByName("omc_dream:soul_boots")
    ),
    SOUL_AXE(
            DreamItemRegistry.getByName("omc_dream:old_creaking_axe"),
            15,
            DreamItemRegistry.getByName("omc_dream:soul_axe")
    ),
    ;

    private final DreamItem input;
    private final int soulsRequired;
    private final DreamItem output;

    AltarRecipes(DreamItem input, int soulsRequired, DreamItem output) {
        this.input = input;
        this.soulsRequired = soulsRequired;
        this.output = output;
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

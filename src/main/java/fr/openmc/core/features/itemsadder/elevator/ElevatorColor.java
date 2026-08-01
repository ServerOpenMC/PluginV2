package fr.openmc.core.features.itemsadder.elevator;

import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum ElevatorColor {

    GRAY(Material.GRAY_DYE,"omc_elevator:elevator_gray"),

    RED(Material.RED_DYE, "omc_elevator:elevator_red"),
    ORANGE(Material.ORANGE_DYE, "omc_elevator:elevator_yellow"),
    YELLOW(Material.YELLOW_DYE, "omc_elevator:elevator_orange"),
    LIME(Material.LIME_DYE, "omc_elevator:elevator_lime"),
    GREEN(Material.GREEN_DYE, "omc_elevator:elevator_green"),
    LIGHT_BLUE(Material.LIGHT_BLUE_DYE, "omc_elevator:elevator_ligh_blue"),
    BLUE(Material.BLUE_DYE, "omc_elevator:elevator_blue"),
    PURPLE(Material.PURPLE_DYE, "omc_elevator:elevator_purple"),
    PINK(Material.PINK_DYE, "omc_elevator:elevator_pink"),
    ;

    private final Material dye;
    private final CustomStack elevator;

    ElevatorColor(Material dye, String elevator) {
        this.dye = dye;
        this.elevator = CustomStack.getInstance(elevator);
    }

}

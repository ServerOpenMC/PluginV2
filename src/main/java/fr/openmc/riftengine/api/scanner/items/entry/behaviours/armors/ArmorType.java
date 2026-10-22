package fr.openmc.riftengine.api.scanner.items.entry.behaviours.armors;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;

@Getter
public enum ArmorType {
    HELMET("helmet", "helmet"),
    CHESTPLATE("chestplate", "chest"),
    LEGGINGS("leggings", "leg"),
    BOOTS("boots", "boot")
    ;

    private final String type;
    private final String shortType;

    ArmorType(String type, String shortType) {
        this.type = type;
        this.shortType = shortType;
    }

    public static ArmorType from(Material material) {
        return Arrays.stream(ArmorType.values())
                .filter(t -> material.name().toLowerCase().contains(t.type))
                .findFirst()
                .orElse(null);
    }

    public String selectLayer(String layer1, String layer2) {
        if (this.equals(ArmorType.LEGGINGS))
            return layer2;
        return layer1;
    }
}

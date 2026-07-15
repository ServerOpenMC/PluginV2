package fr.openmc.core.features.dimopener.data;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class StepDimensionData {

    private String name;
    private String description;
    private Type type;
    private int required;
    private Material material;
    private long cooldownSeconds;

    public StepDimensionData() {
    }

    public enum Type {
        ITEMS,
        MONEY
    }
}

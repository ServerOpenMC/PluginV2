package fr.openmc.core.features.dimopener.data;

import fr.openmc.core.features.dimopener.IStepDimension;
import org.bukkit.Material;

public class StepDimensionData implements IStepDimension {

    private String name;
    private String description;
    private Type type;
    private int required;
    private Material material;
    private long cooldownSeconds;

    public StepDimensionData() {}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int getRequired() {
        return required;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public long getCooldownSeconds() {
        return cooldownSeconds;
    }
}

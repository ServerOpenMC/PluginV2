package fr.openmc.core.features.dimopener.data;

import fr.openmc.core.features.dimopener.IDimension;
import fr.openmc.core.features.dimopener.IStepDimension;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DimensionData implements IDimension {

    private boolean enabled;
    private String name;
    private String description;
    private List<StepDimensionData> steps;

    public DimensionData() {
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<IStepDimension> getDimensionsStep() {
        return steps == null ? List.of() : List.copyOf(steps);
    }

    @Getter
    @Setter
    private transient String id;

    @Override
    public String toString() {
        return id + " {" +
                "enabled=" + enabled + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", steps=" + steps +
                "}";
    }
}
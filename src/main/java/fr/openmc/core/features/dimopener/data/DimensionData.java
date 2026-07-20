package fr.openmc.core.features.dimopener.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DimensionData {

    @Getter
    private boolean enabled;
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private String dimensionName;
    @Getter
    private String requireDimension;
    @Getter
    private String icon;

    private List<StepDimensionData> steps;

    public DimensionData() {
    }

    public List<StepDimensionData> getDimensionsStep() {
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
                ", dimensionName='" + dimensionName + '\'' +
                ", requireDimension='" + requireDimension + '\'' +
                ", icon='" + icon + '\'' +
                ", steps=" + steps +
                "}";
    }
}
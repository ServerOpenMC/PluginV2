package fr.openmc.core.features.dimopener;

import java.util.List;

public interface IDimension {

    boolean isEnabled();

    String getName();

    String getDescription();

    List<IStepDimension> getDimensionsStep();

}

package fr.openmc.core.registry.ambient.contents;

import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.registry.ambient.CustomAmbient;

public class DarkAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "dark_ambient";
    }

    @Override
    public DimensionTypesInjector.DimensionTypeBuilder getDimensionType() {
        return new DimensionTypesInjector.DimensionTypeBuilder()
                .hasCeiling(true)
                .hasSkylight(true);
    }
}

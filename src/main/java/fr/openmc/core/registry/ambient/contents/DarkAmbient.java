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
                .attributes(obj -> {
                    obj.addProperty("visual/ambient_light_color", "#DD37E6");
                    obj.addProperty("visual/sky_color", "#684873");
                    obj.addProperty("visual/sky_light_color", "#3B205E");
                    obj.addProperty("visual/fog_start_distance", 78);
                    obj.addProperty("visual/fog_end_distance", 98);
                    obj.addProperty("visual/sunrise_sunset_color", "#F7BE27C4");
                })
                .hasCeiling(true)
                .hasSkylight(true);
    }
}

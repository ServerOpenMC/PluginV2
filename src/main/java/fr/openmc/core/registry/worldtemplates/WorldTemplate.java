package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;

public abstract class WorldTemplate {
    public abstract String getNamespace();
    public abstract String getId();
    public abstract DimensionTypeBuilder dimensionType();
    public abstract BiomeBuilder biome();

    public String getKey() {
        return getNamespace() + ":" + getId();
    }
}
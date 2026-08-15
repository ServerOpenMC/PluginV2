package fr.openmc.core.features.singularity.contents.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.core.registry.worldtemplates.WorldTemplate;

public class SingularityWorldTemplate extends WorldTemplate {
    @Override
    public String getNamespace() {
        return "omc_singularity";
    }

    @Override
    public String getId() {
        return "singularity_world";
    }

    @Override
    public DimensionTypeBuilder dimensionType() {
        return new DimensionTypeBuilder();
    }

    @Override
    public BiomeBuilder biome() {
        return new BiomeBuilder();
    }
}

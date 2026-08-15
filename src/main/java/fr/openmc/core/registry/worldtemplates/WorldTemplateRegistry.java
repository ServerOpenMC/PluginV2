package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.api.datapacks.builders.dimensions.VoidDimensionBuilder;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.api.datapacks.injectors.DimensionInjector;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.singularity.contents.worldtemplates.SingularityWorldTemplate;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class WorldTemplateRegistry extends Registry<String, WorldTemplate>
        implements KeyedRegistry<String, WorldTemplate> {

    // ** REGISTER WORLD TEMPLATES **
    public final WorldTemplate SINGULARITY_WORLD = register(new SingularityWorldTemplate());

    @Override
    public void bootstrap(BootstrapContext context) throws IOException {
        // * Initialise le dimension type et le biome associé à la map
        for (WorldTemplate template : values()) {
            OMCDatapack worldTemplateDatapack = new OMCDatapack(template.getNamespace());

            DimensionTypesInjector dimTypeInjector = new DimensionTypesInjector(template.getNamespace(), template.getId(), template.dimensionType());
            worldTemplateDatapack.addInjector(dimTypeInjector);
            BiomesInjector biomeInjector = new BiomesInjector(template.getNamespace(), template.getId(), template.biome());
            worldTemplateDatapack.addInjector(biomeInjector);
            worldTemplateDatapack.addInjector(new DimensionInjector(
                    template.getNamespace(),
                    template.getId(),
                    new VoidDimensionBuilder()
                            .biome(biomeInjector)
                            .type(dimTypeInjector)));

            worldTemplateDatapack.buildBootstrap(context, true); // todo: remettre sur false qd fini de debug
        }

        // * Transfère les chunks de la dimension dans son dossier
        for (WorldTemplate template : values()) {
            template.copyToDimensionsFolder(context);
        }
    }

    @Override
    public String key(WorldTemplate registryObject) {
        return registryObject.getKey();
    }
}
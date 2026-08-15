package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.singularity.contents.worldtemplates.SingularityWorldTemplate;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class WorldTemplateRegistry extends Registry<String, WorldTemplate>
        implements KeyedRegistry<String, WorldTemplate> {
    public static final String NAMESPACE = "omc_world_template";
    private final OMCDatapack worldTemplateDatapack = new OMCDatapack(NAMESPACE);

    // ** REGISTER WORLD TEMPLATES **
    public final WorldTemplate SINGULARITY_WORLD = register(new SingularityWorldTemplate());

    @Override
    public void bootstrap(BootstrapContext context) throws IOException {
        // * Initialise le dimension type et le biome associé à la map
        for (WorldTemplate template : values()) {
            worldTemplateDatapack.addInjector(new DimensionTypesInjector(NAMESPACE, template.getId(), template.dimensionType()));
            worldTemplateDatapack.addInjector(new BiomesInjector(NAMESPACE, template.getId(), template.biome()));
        }

        // * Transfère les chunks de la dimension dans son dossier
        for (WorldTemplate template : values()) {
            template.copyToDimensionsFolder(context);
        }

        worldTemplateDatapack.buildBootstrap(context, true); // todo: remettre sur false qd fini de debug
    }

    @Override
    public String key(WorldTemplate registryObject) {
        return registryObject.getKey();
    }
}
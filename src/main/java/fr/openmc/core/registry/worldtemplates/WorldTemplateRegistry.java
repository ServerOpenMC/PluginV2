package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.api.datapacks.builders.dimensions.VoidDimensionBuilder;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.api.datapacks.injectors.DimensionInjector;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.singularity.contents.worldtemplates.SingularityWorldTemplate;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import org.bukkit.World;

import java.io.IOException;
import java.util.Optional;

// todo: faire que dimension se crée une fois si la map n'est pas présente, et pas a chaque redem
@SuppressWarnings("UnstableApiUsage")
public class WorldTemplateRegistry extends Registry<String, WorldTemplate>
        implements KeyedRegistry<String, WorldTemplate> {

    // ** REGISTER WORLD TEMPLATES **
    public final WorldTemplate SINGULARITY_WORLD = register(new SingularityWorldTemplate());

    @Override
    public void bootstrap(BootstrapContext context) throws IOException {
        WorldTemplateConfig.init(context.getDataDirectory().toFile());

        // * Initialise le dimension type et le biome associé à la map
        for (WorldTemplate template : values()) {
            if (!template.isAlreadyCreated(context.getDataDirectory()))
                WorldTemplateConfig.removeBiomeLoaded(template);

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
            if (template.isAlreadyCreated(context.getDataDirectory())) continue;
            template.copyToDimensionsFolder(context);
            OMCLogger.infoFormatted("La map " + template.getKey().asString() + " a été copié avec succès");
        }
    }

    @Override
    public void init() {
        for (WorldTemplate template : values()) {
            if (WorldTemplateConfig.hasBiomeLoaded(template)) continue;

            OMCLogger.infoFormatted("Applique les biomes sur la dimension en cours {}", template.getKey());
            template.applyBiomeOnDimension();
            OMCLogger.successFormatted("Biomes appliqués sur la dimension {}", template.getKey());
            WorldTemplateConfig.addBiomeLoaded(template);
        }
    }

    @Override
    public String key(WorldTemplate registryObject) {
        return registryObject.getKey().asString();
    }

    public WorldTemplate getByWorld(World world) {
        Optional<WorldTemplate> template = get(world.getKey().asString());

        if (template.isEmpty()) return null;
        return template.get();
    }
}
package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.api.datapacks.builders.dimensions.VoidDimensionBuilder;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.api.datapacks.injectors.DimensionInjector;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.singularity.contents.worldtemplates.SingularityWorldTemplate;
import fr.openmc.core.registry.worldtemplates.listeners.ForceBiomeOnTemplateWorldListener;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import org.bukkit.World;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

// todo: faire que dimension se crée une fois si la map n'est pas présente, et pas a chaque redem
@SuppressWarnings("UnstableApiUsage")
public class WorldTemplateRegistry extends Registry<String, WorldTemplate>
        implements KeyedRegistry<String, WorldTemplate>, HasListeners {

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

    public WorldTemplate getByWorld(World world) {
        Optional<WorldTemplate> template = get(world.getKey().asString());

        if (template.isEmpty()) return null;
        return template.get();
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(ForceBiomeOnTemplateWorldListener::new);
    }
}
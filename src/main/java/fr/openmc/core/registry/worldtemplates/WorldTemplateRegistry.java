package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.api.datapacks.builders.dimensions.VoidDimensionBuilder;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.api.datapacks.injectors.DimensionInjector;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.types.HasFeature;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.singularity.contents.worldtemplates.SingularityWorldTemplate;
import fr.openmc.core.registry.worldtemplates.interfaces.HasGamerules;
import fr.openmc.core.registry.worldtemplates.interfaces.HasWorldBorder;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

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
                WorldTemplateConfig.removeFirstLoaded(template);

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
    }

    @Override
    public void init() {
        for (WorldTemplate template : values()) {
            if (template instanceof HasFeature hasFeature)
                OMCPlugin.registerFeature(hasFeature.getFeature());

            World world = template.getWorld();

            // * impl HasGamerules
            if (template instanceof HasGamerules gamerules) {
                Map<GameRule<?>, Object> gamerulesMap = gamerules.getGamerules();
                for (Map.Entry<GameRule<?>, Object> entry : gamerulesMap.entrySet()) {
                    HasGamerules.applyRule(world, entry.getKey(), entry.getValue());
                }
            }

            // * impl hasWorldBorder
            if (template instanceof HasWorldBorder worldBorder) {
                WorldBorder worldBorder1 = world.getWorldBorder();

                worldBorder1.setCenter(worldBorder.getCenter()[0], worldBorder.getCenter()[1]);
                worldBorder1.setSize(worldBorder.getSize());
            }

            if (WorldTemplateConfig.hasFirstLoaded(template)) continue;
            template.firstLoad();
            WorldTemplateConfig.addFirstLoaded(template);
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
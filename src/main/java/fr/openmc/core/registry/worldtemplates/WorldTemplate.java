package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.registry.worldtemplates.interfaces.HasGamerules;
import fr.openmc.core.registry.worldtemplates.interfaces.HasWorldBorder;
import fr.openmc.core.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftWorld;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

// todo: add interface,  ...
@SuppressWarnings("UnstableApiUsage")
public abstract class WorldTemplate {
    private static Registry<Biome> BIOME_REGISTRY = null;
    private World world = null;

    // * a @Override
    public void onFirstLoad() {
        World world = getWorld();

        // * impl HasGamerules
        if (this instanceof HasGamerules gamerules) {
            Map<GameRule<?>, Object> gamerulesMap = gamerules.getGamerules();
            for (Map.Entry<GameRule<?>, Object> entry : gamerulesMap.entrySet()) {
                HasGamerules.applyRule(world, entry.getKey(), entry.getValue());
            }
        }

        // * impl hasWorldBorder
        if (this instanceof HasWorldBorder worldBorder) {
            WorldBorder worldBorder1 = world.getWorldBorder();

            worldBorder1.setCenter(worldBorder.getCenter()[0], worldBorder.getCenter()[1]);
            worldBorder1.setSize(worldBorder.getSize());
        }
    }

    public abstract String getNamespace();
    public abstract String getId();
    public abstract DimensionTypeBuilder dimensionType();
    public abstract BiomeBuilder biome();

    public Biome getBiome() {
        if (BIOME_REGISTRY == null)
            BIOME_REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
        return BIOME_REGISTRY.getOrThrow(getKey());
    }

    public Holder<net.minecraft.world.level.biome.Biome> getNMSBiome() {
        ServerLevel level = ((CraftWorld) world).getHandle();
        return level.registryAccess()
                .lookupOrThrow(Registries.BIOME)
                .get(Identifier.parse(getKey().asString())).orElseThrow();
    }

    public World getWorld() {
        if (world == null)
            world = Bukkit.getWorld(getKey());
        return world;
    }

    public NamespacedKey getKey() {
        return NamespacedKey.fromString(getNamespace() + ":" + getId());
    }

    public void copyToDimensionsFolder(BootstrapContext context) {
        try {
            File pluginsDir = context.getDataDirectory().toFile().getParentFile().getParentFile(); // * root
            File worldDir = new File(pluginsDir, "world"); // * root/world
            File dimensionsDir = new File(worldDir, "dimensions"); // * root/world/dimensions
            File namespaceDir = new File(dimensionsDir, getNamespace()); // * root/world/dimensions/<namespace>/
            File idDir = new File(namespaceDir, getId()); // * root/world/dimensions/<namespace>/<id>
            File regionDir = new File(idDir, "region"); // * root/world/dimensions/<namespace>/<id>/region

            FilesUtils.copyResourceFolder("world_template/" + getNamespace() + "/" + getId(), regionDir);
        } catch (Exception e) {
            OMCLogger.warn("Erreur lors de la copie du dossier {}: {}", getNamespace() + "/" + getId(), e.getMessage());
        }
    }

    public boolean isAlreadyCreated(Path dataPath) {
        File pluginsDir = dataPath.toFile().getParentFile().getParentFile(); // * root
        File worldDir = new File(pluginsDir, "world"); // * root/world
        File dimensionsDir = new File(worldDir, "dimensions"); // * root/world/dimensions
        File namespaceDir = new File(dimensionsDir, getNamespace()); // * root/world/dimensions/<namespace>/
        File idDir = new File(namespaceDir, getId()); // * root/world/dimensions/<namespace>/<id>
        File dataDir = new File(idDir, "data"); // * root/world/dimensions/<namespace>/<id>/data

        return dataDir.exists() && dataDir.isDirectory();
    }
}
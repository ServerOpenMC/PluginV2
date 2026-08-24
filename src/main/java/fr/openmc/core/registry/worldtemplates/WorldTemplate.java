package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public abstract class WorldTemplate {
    private static Registry<Biome> BIOME_REGISTRY = null;
    private World world = null;

    // * a @Override
    public void onFirstLoad() {}

    public void firstLoad() {
        onFirstLoad();
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

    public World getWorld() {
        if (world == null)
            world = Bukkit.getWorld(getKey());
        return world;
    }

    public NamespacedKey getKey() {
        return NamespacedKey.fromString(getNamespace() + ":" + getId());
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
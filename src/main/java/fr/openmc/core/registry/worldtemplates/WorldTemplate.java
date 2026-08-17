package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.utils.FilesUtils;
import fr.openmc.core.utils.nms.world.WorldBiomeNMS;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftWorld;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// todo: add interface, GameruleWorld, onFirstStart, ...
@SuppressWarnings("UnstableApiUsage")
public abstract class WorldTemplate {
    private static Registry<Biome> BIOME_REGISTRY = null;
    private World world = null;

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

    /**
     * Applique le biome de la template du monde
     *
     * CONSOMME BEAUCOUP DE RESSOURCES, A UTILISER AVEC PRECAUSIONS
     */
    public void applyBiomeOnDimension() {
        File regionFolder = new File(getWorld().getWorldFolder(), "region");

        File[] files = regionFolder.listFiles((dir, name) ->
                name.matches("r\\.-?\\d+\\.-?\\d+\\.mca"));
        if (files == null) return;

        Pattern pattern = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.mca");

        for (File file : files) {
            Matcher matcher = pattern.matcher(file.getName());
            if (!matcher.matches()) continue;

            int regionX = Integer.parseInt(matcher.group(1));
            int regionZ = Integer.parseInt(matcher.group(2));

            int minChunkX = regionX << 5;
            int maxChunkX = minChunkX + 31;
            int minChunkZ = regionZ << 5;
            int maxChunkZ = minChunkZ + 31;

            WorldBiomeNMS.setWorldBiome(world, minChunkX, maxChunkX, minChunkZ, maxChunkZ, getNMSBiome());
        }
    }
}
package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.block.Biome;

import java.io.File;

// todo: add interface, GameruleWorld, onFirstStart, ...
@SuppressWarnings("UnstableApiUsage")
public abstract class WorldTemplate {
    private static Registry<Biome> BIOME_REGISTRY = null;
    public abstract String getNamespace();
    public abstract String getId();
    public abstract DimensionTypeBuilder dimensionType();
    public abstract BiomeBuilder biome();

    public Biome getBiome() {
        if (BIOME_REGISTRY == null)
            BIOME_REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
        return BIOME_REGISTRY.getOrThrow(Key.key(getKey()));
    }

    public String getKey() {
        return getNamespace() + ":" + getId();
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
}
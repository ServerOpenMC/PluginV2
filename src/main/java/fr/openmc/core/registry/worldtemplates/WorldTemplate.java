package fr.openmc.core.registry.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.File;

@SuppressWarnings("UnstableApiUsage")
public abstract class WorldTemplate {
    public abstract String getNamespace();
    public abstract String getId();
    public abstract DimensionTypeBuilder dimensionType();
    public abstract BiomeBuilder biome();

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
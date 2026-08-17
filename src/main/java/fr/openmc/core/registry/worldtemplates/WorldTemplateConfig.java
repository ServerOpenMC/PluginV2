package fr.openmc.core.registry.worldtemplates;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldTemplateConfig {
    private static File worldTemplateFile;
    private static FileConfiguration worldTemplateConfig;

    public static void init(File dataFolder) {
        worldTemplateFile = new File(dataFolder + "/data/registry", "world_template.yml");
        worldTemplateConfig = YamlConfiguration.loadConfiguration(worldTemplateFile);

        // * Premier lancement du plugin où suppression du fichier par une classe externe (ex CustomAmbientRegistry)
        if (!worldTemplateFile.exists()) {
            worldTemplateConfig.set("biomes_loaded", new ArrayList<>());
            saveConfig();
        }
    }

    public static boolean hasBiomeLoaded(WorldTemplate template) {
        return worldTemplateConfig.getStringList("biomes_loaded").contains(template.getKey().asString());
    }

    public static void removeBiomeLoaded(WorldTemplate template) {
        List<String> biomesLoaded = worldTemplateConfig.getStringList("biomes_loaded");
        biomesLoaded.remove(template.getKey().asString());
        worldTemplateConfig.set("biomes_loaded", biomesLoaded);
        saveConfig();
    }

    private static void saveConfig() {
        try {
            worldTemplateConfig.save(worldTemplateFile);
        } catch (IOException e) {
            OMCLogger.error("Cannot save registriesConfigFile", e);
        }
    }
}

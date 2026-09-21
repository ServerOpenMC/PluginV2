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
            worldTemplateConfig.set("first_loaded", new ArrayList<>());
            saveConfig();
        }
    }

    public static boolean hasFirstLoaded(WorldTemplate template) {
        return worldTemplateConfig.getStringList("first_loaded").contains(template.getKey().asString());
    }

    public static void addFirstLoaded(WorldTemplate template) {
        List<String> biomesLoaded = worldTemplateConfig.getStringList("first_loaded");
        biomesLoaded.add(template.getKey().asString());
        worldTemplateConfig.set("first_loaded", biomesLoaded);
        saveConfig();
    }

    public static void removeFirstLoaded(WorldTemplate template) {
        List<String> biomesLoaded = worldTemplateConfig.getStringList("first_loaded");
        biomesLoaded.remove(template.getKey().asString());
        worldTemplateConfig.set("first_loaded", biomesLoaded);
        saveConfig();
    }

    private static void saveConfig() {
        try {
            worldTemplateConfig.save(worldTemplateFile);
        } catch (IOException e) {
            OMCLogger.error("Cannot save worldTemplateConfigFile", e);
        }
    }
}

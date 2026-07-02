package fr.openmc.core.registry.ambient;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Cette configuration gère le restart de serveur apres la génération du datapack dans le runtime
 * <p>
 * Explication :
 * - Lorsqu'on génère un datapack dans le runtime, les changements ne sont pas appliqués dans les registry de minecraft
 * - C'est pourquoi il faut redem le serveur afin que le datapack puisse bien entrer dans le registre.
 * - Donc cela nécessite 2 lancements pour faire tourner une premiere fois le plugin.
 */
public class RegistriesLoadConfig {
    private static File registriesConfigFile;
    private static FileConfiguration registriesConfig;
    @Getter
    private static boolean mustRestart;

    public static void init(File dataFolder) {
        registriesConfigFile = new File(dataFolder + "/data/registry", "load.yml");
        registriesConfig = YamlConfiguration.loadConfiguration(registriesConfigFile);

        // * Premier lancement du plugin où suppression du fichier par une classe externe (ex CustomAmbientRegistry)
        if (!registriesConfigFile.exists()) {
            mustRestart = true;
            saveConfig();
        } else {
            mustRestart = false;
        }
    }

    private static void saveConfig() {
        try {
            registriesConfig.save(registriesConfigFile);
        } catch (IOException e) {
            OMCLogger.error("Cannot save registriesConfigFile", e);
        }
    }
}

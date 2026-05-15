package fr.openmc.core.features.displays.bossbar.contents;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Charge et expose les messages d'aide affichés dans les boss bars.
 */
public class HelpConfigManager extends Feature {

    @Getter
    private static final List<Component> helpMessages = new ArrayList<>();
    @Getter
    private static File configFile;

    /**
     * Initialise le gestionnaire en chargeant la configuration et les messages par défaut.
     */
    @Override
    public void init() {
        configFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "bossbars.yml");
        loadConfig();
        loadDefaultMessages();
    }

    @Override
    protected void save() {
        // not used
    }

    /**
     * Charge la configuration depuis bossbars.yml et crée le fichier si nécessaire.
     */
    private static void loadConfig() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/bossbars.yml", false);
        }
        reloadMessages();
    }

    /**
     * Charge les messages depuis le fichier de configuration.
     */
    private static void loadDefaultMessages() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        helpMessages.clear();

        for (String rawMessage : config.getStringList("messages")) {
            helpMessages.add(MiniMessage.miniMessage().deserialize(rawMessage));
        }

        if (helpMessages.isEmpty()) {
            OMCLogger.warn("No messages found in bossbars.yml.");
        }
    }

    /**
     * Recharge les messages depuis le fichier de configuration.
     */
    public static void reloadMessages() {
        helpMessages.clear();
        loadDefaultMessages();
    }
}

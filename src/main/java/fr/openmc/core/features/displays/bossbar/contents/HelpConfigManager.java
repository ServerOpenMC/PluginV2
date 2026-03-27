package fr.openmc.core.features.displays.bossbar.contents;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.displays.bossbar.commands.BossBarCommand;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HelpConfigManager {

    @Getter
    private static final List<Component> helpMessages = new ArrayList<>();
    @Getter
    private static File configFile;

    /**
     * Initializes the HelpConfigManager by loading the configuration and default messages
     */
    public static void init() {
        configFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "bossbars.yml");
        loadConfig();
        loadDefaultMessages();
    }

    /**
     * Loads configuration from bossbars.yml file
     * Creates the file if it doesn't exist
     */
    private static void loadConfig() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/bossbars.yml", false);
        }
        reloadMessages();
    }

    /**
     * Loads messages from the configuration file
     */
    private static void loadDefaultMessages() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        helpMessages.clear();

        for (String rawMessage : config.getStringList("messages")) {
            helpMessages.add(MiniMessage.miniMessage().deserialize(rawMessage));
        }

        if (helpMessages.isEmpty()) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("No messages found in bossbars.yml.");
        }
    }

    /**
     * Reloads messages from the configuration file
     */
    public static void reloadMessages() {
        helpMessages.clear();
        loadDefaultMessages();
    }
}

package fr.openmc.core.features.bossbar;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.bossbar.commands.BossBarCommand;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import lombok.Getter;

import java.io.File;
import java.util.*;

public class BossbarManager {
    @Getter
    private static BossbarManager instance;
    private final Map<UUID, BossBar> activeBossBars = new HashMap<>();
    private final Map<UUID, Boolean> playerPreferences = new HashMap<>();
    @Getter
    private final List<Component> helpMessages = new ArrayList<>();
    @Getter
    private boolean bossBarEnabled = true;
    @Getter
    private final File configFile;
    private int currentMessageIndex = 0;
    @Getter
    private final OMCPlugin plugin;

    /**
     * Constructs the BossbarManager and initializes its components
     * @param plugin The main plugin instance
     */
    public BossbarManager(OMCPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.configFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "bossbars.yml");
        loadConfig();
        loadDefaultMessages();
        startRotationTask();
        CommandsManager.getHandler().register(new BossBarCommand());
    }

    /**
     * Loads configuration from bossbars.yml file
     * Creates the file if it doesn't exist
     */
    private void loadConfig() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/bossbars.yml", false);
        }
        reloadMessages();
    }

    /**
     * Loads messages from the configuration file
     */
    private void loadDefaultMessages() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        helpMessages.clear();

        for (String rawMessage : config.getStringList("messages")) {
            helpMessages.add(MiniMessage.miniMessage().deserialize(rawMessage));
        }

        if (helpMessages.isEmpty()) {
            plugin.getLogger().warning("Aucun message trouvé - vérifiez bossbars.yml");
        }
    }

    /**
     * Starts the message rotation task for bossbars
     * Messages change every 10 seconds (200 ticks)
     */
    private void startRotationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (helpMessages.isEmpty()) return;

                currentMessageIndex = (currentMessageIndex + 1) % helpMessages.size();
                Component message = helpMessages.get(currentMessageIndex);

                activeBossBars.forEach((uuid, bossBar) -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        bossBar.name(message);
                    }
                });
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 200);
    }

    /**
     * Adds a bossbar to the specified player
     * @param player The player to add the bossbar to
     */
    public void addBossBar(Player player) {
        if (!bossBarEnabled || activeBossBars.containsKey(player.getUniqueId())) return;

        Boolean preference = playerPreferences.get(player.getUniqueId());
        if (preference != null && !preference) {
            return;
        }
        removeBossBar(player);

        BossBar bossBar = BossBar.bossBar(
                helpMessages.get(0),
                0f,
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
        );

        player.showBossBar(bossBar);
        activeBossBars.put(player.getUniqueId(), bossBar);
    }

    /**
     * Removes the bossbar from the specified player
     * @param player The player to remove the bossbar from
     */
    public void removeBossBar(Player player) {
        BossBar bossBar = activeBossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            player.hideBossBar(bossBar);
        }
    }

    /**
     * Toggles the bossbar for a specific player
     * @param player The player to toggle the bossbar for
     */
    public void toggleBossBar(Player player) {
        UUID uuid = player.getUniqueId();

        if (activeBossBars.containsKey(player.getUniqueId())) {
            removeBossBar(player);
            playerPreferences.put(uuid, false);
            MessagesManager.sendMessage(player, Component.text("Bossbar désactivée"), Prefix.OPENMC, MessageType.WARNING, true);
        } else {
            addBossBar(player);
            playerPreferences.put(uuid, true);
            MessagesManager.sendMessage(player, Component.text("Bossbar activée"), Prefix.OPENMC, MessageType.SUCCESS, true);
        }
    }

    /**
     * Reloads messages from the configuration file
     */
    public void reloadMessages() {
        helpMessages.clear();
        loadDefaultMessages();
    }

    /**
     * Checks if bossbars are globally enabled
     * @return true if bossbars are enabled, false otherwise
     */
    public boolean hasBossBar() {
        return bossBarEnabled;
    }

    /**
     * Sets the list of messages to display in bossbars
     * @param messages The list of new messages
     */
    public void setHelpMessages(List<Component> messages) {
        helpMessages.clear();
        helpMessages.addAll(messages);
        saveMessagesToConfig();
    }

    /**
     * Saves messages to the configuration file
     */
    private void saveMessagesToConfig() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            List<String> serializedMessages = new ArrayList<>();

            for (Component message : helpMessages) {
                serializedMessages.add(MiniMessage.miniMessage().serialize(message));
            }

            config.set("messages", serializedMessages);
            config.save(configFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde des messages: " + e.getMessage());
        }
    }

    /**
     * Adds a message to the message list
     * @param message The message to add
     */
    public void addMessage(Component message) {
        helpMessages.add(message);
        saveMessagesToConfig();
    }

    /**
     * Removes a message from the message list
     * @param index The index of the message to remove
     */
    public void removeMessage(int index) {
        if (index >= 0 && index < helpMessages.size()) {
            helpMessages.remove(index);
            saveMessagesToConfig();
        }
    }

    /**
     * Updates an existing message
     * @param index The index of the message to update
     * @param newMessage The new message content
     */
    public void updateMessage(int index, Component newMessage) {
        if (index >= 0 && index < helpMessages.size()) {
            helpMessages.set(index, newMessage);
            saveMessagesToConfig();
        }
    }

    /**
     * Toggles bossbars globally for all players
     */
    public void toggleGlobalBossBar() {
        bossBarEnabled = !bossBarEnabled;

        if (bossBarEnabled) {
            Bukkit.getOnlinePlayers().forEach(this::addBossBar);
        } else {
            Bukkit.getOnlinePlayers().forEach(this::removeBossBar);
        }
    }
}
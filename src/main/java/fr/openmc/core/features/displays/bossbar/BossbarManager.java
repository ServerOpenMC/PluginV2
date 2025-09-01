package fr.openmc.core.features.displays.bossbar;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.displays.bossbar.commands.BossBarCommand;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class BossbarManager {
    @Getter
    private static final List<Component> helpMessages = new ArrayList<>();
    private static final HashMap<BossbarsType, Map<UUID, BossBar>> activeBossBars = new HashMap<>();
    private static final Map<UUID, Boolean> playerPreferences = new HashMap<>();
    @Getter
    private static boolean bossBarEnabled = true;
    @Getter
    private static File configFile;
    private static int currentMessageIndex = 0;

    public static BossBar bossBarHelp;

    /**
     * Constructs the BossbarManager and initializes its components
     */
    public BossbarManager() {
        configFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "bossbars.yml");
        loadConfig();
        loadDefaultMessages();
        startRotationTask();
        CommandsManager.getHandler().register(new BossBarCommand());

        bossBarHelp = BossBar.bossBar(
                helpMessages.getFirst(),
                0f,
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
        );
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
     * Starts the message rotation task for bossbars
     * Messages change every 10 seconds (200 ticks)
     */
    private static void startRotationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (helpMessages.isEmpty()) return;

                currentMessageIndex = (currentMessageIndex + 1) % helpMessages.size();
                Component message = helpMessages.get(currentMessageIndex);

                activeBossBars.forEach((type, bossBarData) -> {
                    if (!type.equals(BossbarsType.HELP)) return;

                    bossBarData.forEach((uuid, bossBar) -> {
                        if (bossBar != null) {
                            bossBar.name(message);
                        }
                    });
                });
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 200);
    }

    /**
     * Adds a bossbar to the specified player
     * @param type The type of bossbar to add
     * @param bossbar The bossbar to add
     * @param player The player to add the bossbar to
     */
    public static void addBossBar(BossbarsType type, BossBar bossbar, Player player) {
        if (!bossBarEnabled) return;

        Map<UUID, BossBar> bars = activeBossBars.get(type);
        if (bars != null && bars.containsKey(player.getUniqueId())) return;

        Boolean preference = playerPreferences.get(player.getUniqueId());
        if (preference != null && !preference) {
            return;
        }
        removeBossBar(type, player);

        player.showBossBar(bossbar);
        activeBossBars.computeIfAbsent(type, k -> new HashMap<>()).put(player.getUniqueId(), bossbar);
    }

    /**
     * Removes the bossbar from the specified player
     * @param type The type of bossbar to remove
     * @param player The player to remove the bossbar from
     */
    public static void removeBossBar(BossbarsType type, Player player) {
        Map<UUID, BossBar> map = activeBossBars.get(type);
        if (map == null) return;

        BossBar bossBar = map.remove(player.getUniqueId());
        if (bossBar != null) {
            player.hideBossBar(bossBar);
        }
    }

    /**
     * Gets the bossbar for a specific player
     *
     * @param type   The type of bossbar to get
     * @param player The player to get the bossbar for
     * @return The bossbar for the specified player, or null if not found
     */
    public static BossBar getBossBar(BossbarsType type, Player player) {
        return activeBossBars.get(type).get(player.getUniqueId());

    }

    /**
     * Toggles the bossbar for a specific player
     * @param player The player to toggle the bossbar for
     */
    public static void toggleBossBar(Player player) {
        UUID uuid = player.getUniqueId();
        boolean enabled = false;

        for (Map.Entry<BossbarsType, Map<UUID, BossBar>> activeBossBar : activeBossBars.entrySet()) {
            BossbarsType type = activeBossBar.getKey();
            Map<UUID, BossBar> bossBarData = activeBossBar.getValue();
            if (bossBarData.containsKey(uuid)) {
                removeBossBar(type, player);
                enabled = false;
                playerPreferences.put(uuid, false);
            } else {
                switch (type) {
                    case HELP -> {
                        addBossBar(type, bossBarHelp, player);
                        enabled = true;
                        playerPreferences.put(uuid, true);
                    }

                    case TUTORIAL -> {
                        TutorialUtils.setBossBar(player);
                        enabled = true;
                        playerPreferences.put(uuid, true);
                    }
                }
            }
        }

        if (enabled) {
            MessagesManager.sendMessage(player, Component.text("Bossbar activée"), Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessage(player, Component.text("Bossbar désactivée"), Prefix.OPENMC, MessageType.WARNING, true);
        }
    }

    /**
     * Reloads messages from the configuration file
     */
    public static void reloadMessages() {
        helpMessages.clear();
        loadDefaultMessages();
    }

    /**
     * Checks if bossbars are globally enabled
     * @return true if bossbars are enabled, false otherwise
     */
    public static boolean hasBossBar() {
        return bossBarEnabled;
    }

    /**
     * Sets the list of messages to display in bossbars
     * @param messages The list of new messages
     */
    public static void setHelpMessages(List<Component> messages) {
        helpMessages.clear();
        helpMessages.addAll(messages);
        saveMessagesToConfig();
    }

    /**
     * Saves messages to the configuration file
     */
    private static void saveMessagesToConfig() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            List<String> serializedMessages = new ArrayList<>();

            for (Component message : helpMessages) {
                serializedMessages.add(MiniMessage.miniMessage().serialize(message));
            }

            config.set("messages", serializedMessages);
            config.save(configFile);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Failed to save bossbar messages to config: {}", e.getMessage(), e);
        }
    }

    /**
     * Adds a message to the message list
     * @param message The message to add
     */
    public static void addMessage(Component message) {
        helpMessages.add(message);
        saveMessagesToConfig();
    }

    /**
     * Removes a message from the message list
     * @param index The index of the message to remove
     */
    public static void removeMessage(int index) {
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
    public static void updateMessage(int index, Component newMessage) {
        if (index >= 0 && index < helpMessages.size()) {
            helpMessages.set(index, newMessage);
            saveMessagesToConfig();
        }
    }

    /**
     * Toggles bossbars globally for all players
     */
    public static void toggleGlobalBossBar() {
        bossBarEnabled = !bossBarEnabled;

        if (bossBarEnabled) {
            Bukkit.getOnlinePlayers().forEach(player -> addBossBar(BossbarsType.HELP, bossBarHelp, player));
        } else {
            Bukkit.getOnlinePlayers().forEach(player -> removeBossBar(BossbarsType.HELP, player));
        }
    }
}

package fr.openmc.core.features.bossbar;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.accountdetection.commands.AccountDetectionCommand;
import fr.openmc.core.features.bossbar.commands.BossBarCommand;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    private final List<Component> helpMessages = new ArrayList<>();
    @Getter
    private boolean bossBarEnabled = true;
    @Getter
    private final File configFile;
    private int currentMessageIndex = 0;
    @Getter
    private final OMCPlugin plugin;


    public BossbarManager(OMCPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.configFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "bossbars.yml");
        loadConfig();
        loadDefaultMessages();
        startRotationTask();
        CommandsManager.getHandler().register(new BossBarCommand());
    }

    private void loadConfig() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/bossbars.yml", false);
        }
        reloadMessages();
    }

    private void loadDefaultMessages() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        helpMessages.clear();

        for (String rawMessage : config.getStringList("messages")) {
            helpMessages.add(MiniMessage.miniMessage().deserialize(rawMessage));
        }

        if (helpMessages.isEmpty()) {
            plugin.getLogger().warning("Aucun message chargé - vérifiez bossbars.yml");
        }
    }

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
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 200); // Change toutes les 10 secondes (200 ticks)
    }

    public void addBossBar(Player player) {
        if (!bossBarEnabled || activeBossBars.containsKey(player.getUniqueId())) return;

        removeBossBar(player);

        BossBar bossBar = BossBar.bossBar(
                helpMessages.get(0),
                1.0f,
                BossBar.Color.PINK,
                BossBar.Overlay.PROGRESS
        );

        player.showBossBar(bossBar);
        activeBossBars.put(player.getUniqueId(), bossBar);
    }

    public void removeBossBar(Player player) {
        BossBar bossBar = activeBossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            player.hideBossBar(bossBar);
        }
    }


    public void toggleBossBar(Player player) {
        if (activeBossBars.containsKey(player.getUniqueId())) {
            removeBossBar(player);
            player.sendMessage(Component.text("Bossbar désactivée").color(NamedTextColor.RED));
        } else {
            addBossBar(player);
            player.sendMessage(Component.text("Bossbar activée").color(NamedTextColor.GREEN));
        }
    }

    public void reloadMessages() {
        helpMessages.clear();
        loadDefaultMessages();
    }

    public boolean hasBossBar() {
        return bossBarEnabled;
    }

    public List<Component> getHelpMessages() {
        return new ArrayList<>(helpMessages);
    }

    public void setHelpMessages(List<Component> messages) {
        helpMessages.clear();
        helpMessages.addAll(messages);
        saveMessagesToConfig();
    }

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

    public void addMessage(Component message) {
        helpMessages.add(message);
        saveMessagesToConfig();
    }

    public void removeMessage(int index) {
        if (index >= 0 && index < helpMessages.size()) {
            helpMessages.remove(index);
            saveMessagesToConfig();
        }
    }

    public void updateMessage(int index, Component newMessage) {
        if (index >= 0 && index < helpMessages.size()) {
            helpMessages.set(index, newMessage);
            saveMessagesToConfig();
        }
    }

    public void toggleGlobalBossBar() {
        bossBarEnabled = !bossBarEnabled;

        if (bossBarEnabled) {
            Bukkit.getOnlinePlayers().forEach(this::addBossBar);
        } else {
            Bukkit.getOnlinePlayers().forEach(this::removeBossBar);
        }
    }
}
package fr.openmc.core;

import fr.openmc.api.input.ChatInput;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.bossbar.listeners.BossbarListener;
import fr.openmc.core.features.mailboxes.MailboxListener;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.updates.UpdateListener;
import fr.openmc.core.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenersManager {
    public ListenersManager() {
        registerEvents(
                new SessionsListener(),
                new JoinMessageListener(),
                new UpdateListener(),
                new ClockInfos(),
                new MailboxListener(),
                new ChronometerListener(),
                new CubeListener(OMCPlugin.getInstance()),
                new ItemInteraction(),
                new ChatInput(),
                new RespawnListener(),
                new SleepListener(),
                new PlayerDeathListener(),
                new AsyncChatListener(OMCPlugin.getInstance()),
                new BossbarListener(),
                new PlayerSettingsManager()
        );
    }

    private static void registerEvents(Listener... args) {
        Server server = Bukkit.getServer();
        JavaPlugin plugin = OMCPlugin.getInstance();
        for (Listener listener : args) {
            server.getPluginManager().registerEvents(listener, plugin);
        }
    }
}

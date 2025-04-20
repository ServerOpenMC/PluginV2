package fr.openmc.core;

import fr.openmc.core.features.mailboxes.MailboxListener;
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
                new ClockInfos(),
                new MailboxListener(),
                new DisableCreakings(),
                new ChronometerListener(),
                new CubeListener(OMCPlugin.getInstance()),
                new RespawnListener(),
                new AsyncChatListener(OMCPlugin.getInstance())
        );
    }

    private void registerEvents(Listener... args) {
        Server server = Bukkit.getServer();
        JavaPlugin plugin = OMCPlugin.getInstance();
        for (Listener listener : args) {
            server.getPluginManager().registerEvents(listener, plugin);
        }
    }
}

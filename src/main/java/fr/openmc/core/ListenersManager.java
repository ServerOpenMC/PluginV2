package fr.openmc.core;

import fr.openmc.api.input.ChatInput;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.city.mayor.managers.NPCManager;
import fr.openmc.core.features.mailboxes.MailboxListener;
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
                new CubeListener(OMCPlugin.getInstance()),
                new RespawnListener(),
                new SleepListener(),
                new PlayerDeadListener(),
                new AsyncChatListener(OMCPlugin.getInstance()),
                new NPCManager(),
                new PlayerDeathListener(),
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

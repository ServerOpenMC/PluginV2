package fr.openmc.core;

import fr.openmc.core.features.mailboxes.MailboxListener;
import fr.openmc.core.features.updates.UpdateListener;
import fr.openmc.core.listeners.*;
import fr.openmc.core.utils.interactions.items.ItemInteraction;
import fr.openmc.core.utils.interactions.text.LongTextInput;
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
                new LongTextInput(),
                new CubeListener(OMCPlugin.getInstance()),
                new RespawnListener(),
                new SleepListener(),
                new PlayerDeadListener(),
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

package fr.openmc.core;

import fr.openmc.api.input.ChatInput;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.cube.listeners.CubeListener;
import fr.openmc.core.features.cube.listeners.RepulseEffectListener;
import fr.openmc.core.features.cube.multiblocks.MultiBlocksListeners;
import fr.openmc.core.features.displays.bossbar.listeners.BossbarListener;
import fr.openmc.core.features.mailboxes.MailboxListener;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.tickets.TicketListener;
import fr.openmc.core.features.updates.UpdateListener;
import fr.openmc.core.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenersManager {
    public ListenersManager() {
        registerEvents(
                new HappyGhastListener(),
                new SessionsListener(),
                new JoinQuitMessageListener(),
                new UpdateListener(),
                new ClockInfos(),
                new MailboxListener(),
                new ChronometerListener(),
                new CubeListener(),
                new RepulseEffectListener(),
                new MultiBlocksListeners(),
                new ItemInteraction(),
                new ChatInput(),
                new RespawnListener(),
                new SleepListener(),
                new PlayerDeathListener(),
                new AsyncChatListener(OMCPlugin.getInstance()),
                new BossbarListener(),
                new PlayerSettingsManager(),
                new InteractListener(),
                new ItemsAddersListener(),
                new TicketListener(),
                new AywenCapListener(),
                new ArmorListener()
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

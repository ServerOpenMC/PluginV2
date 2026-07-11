package fr.openmc.core;

import fr.openmc.api.input.ChatInput;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.combat.CombatCooldownListener;
import fr.openmc.core.features.itemsadder.SpawnerExtractorListener;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.listeners.ArmorListener;
import fr.openmc.core.listeners.AsyncChatListener;
import fr.openmc.core.listeners.BlockBreakListener;
import fr.openmc.core.listeners.BlockPlaceListener;
import fr.openmc.core.listeners.ChronometerListener;
import fr.openmc.core.listeners.ClockInfos;
import fr.openmc.core.listeners.EquipableItemListener;
import fr.openmc.core.listeners.HappyGhastListener;
import fr.openmc.core.listeners.InteractListener;
import fr.openmc.core.listeners.ItemsAddersListener;
import fr.openmc.core.listeners.JoinQuitMessageListener;
import fr.openmc.core.listeners.NoMoreRabbit;
import fr.openmc.core.listeners.PlayerDeathListener;
import fr.openmc.core.listeners.SessionsListener;
import fr.openmc.core.listeners.SleepListener;
import fr.openmc.core.registry.ambient.listeners.AmbientWeatherListener;
import fr.openmc.core.registry.ambient.listeners.BiomesOnChunkLoad;
import fr.openmc.core.registry.ambient.listeners.CustomAmbientListener;
import fr.openmc.core.registry.mobs.listeners.CustomMobDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Centralise l'enregistrement des listeners Bukkit du plugin.
 */
public class ListenersManager {
    /**
     * Enregistre les listeners de base, puis ceux conditionnels (tests, hooks).
     */
    public static void init() {
        registerEvents(
                new HappyGhastListener(),
                new CombatCooldownListener(),
                new SessionsListener(),
                new JoinQuitMessageListener(),
                new ClockInfos(),
                new ChronometerListener(),
                new ItemInteraction(),
                new ChatInput(),
                new SleepListener(),
                new PlayerDeathListener(),
                new AsyncChatListener(OMCPlugin.getInstance()),
                new InteractListener(),
                new BlockPlaceListener(),
                new EquipableItemListener(),
                new NoMoreRabbit(),
                new CustomMobDeathListener(),
                new ArmorListener(),
                new BlockBreakListener(),
                new CustomAmbientListener(),
                new BiomesOnChunkLoad(),
                new AmbientWeatherListener()
        );

        if (!OMCPlugin.isUnitTestVersion()) {
            registerEvents(
                    new SpawnerExtractorListener()
            );
        }

        if (ItemsAdderHook.isEnable()) {
            registerEvents(new ItemsAddersListener());
        }
    }

    /**
     * Enregistre une liste de listeners sur le plugin courant.
     *
     * @param args Listeners a enregistrer
     */
    private static void registerEvents(Listener... args) {
        Server server = Bukkit.getServer();
        JavaPlugin plugin = OMCPlugin.getInstance();
        for (Listener listener : args) {
            server.getPluginManager().registerEvents(listener, plugin);
        }
    }
}

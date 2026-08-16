package fr.openmc.core;

import fr.openmc.api.input.ChatInput;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.itemsadder.SpawnerExtractorListener;
import fr.openmc.core.listeners.*;
import fr.openmc.core.utils.nms.entity.EntityGlowNMS;

/**
 * Centralise l'enregistrement des listeners Bukkit du plugin.
 */
public class ListenersManager {
    /**
     * Enregistre les listeners.
     */
    public static void init() {
        // () -> : nécessaire si y'a un package d'api externe (ex com.comphenix.protocol)
        registerEvents(
                OMCPlayerCacheListener::new,
                HappyGhastListener::new,
                SessionsListener::new,
                JoinQuitMessageListener::new,
                ClockInfos::new,
                ChronometerListener::new,
                ItemInteraction::new,
                ChatInput::new,
                SleepListener::new,
                PlayerDeathListener::new,
                () -> new AsyncChatListener(OMCPlugin.getInstance()),
                NoMoreRabbit::new,
                ArmorListener::new,
                () -> new EntityGlowNMS(),
                () -> new RegionTrackingListener(),
                () -> new SpawnerExtractorListener(),
                () -> new ItemsAddersListener()
        );
    }

    /**
     * Enregistre une liste de listeners sur le plugin courant.
     *
     * @param listeners Listeners a enregistrer
     */
    public static void registerEvents(ListenerFactory... listeners) {
        for (ListenerFactory listenerFactory : listeners) {
            listenerFactory.create(true);
        }
    }
}

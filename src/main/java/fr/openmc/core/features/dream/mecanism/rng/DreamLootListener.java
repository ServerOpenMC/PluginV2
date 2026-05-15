package fr.openmc.core.features.dream.mecanism.rng;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DreamLootListener implements Listener {
    @EventHandler
    public void onLootItem(DreamRngLootEvent event) {
        DreamRngLootManager.sendMessageLoot(event);
    }
}

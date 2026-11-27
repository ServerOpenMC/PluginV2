package fr.openmc.core.features.city.sub.war.listeners;

import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class WarVisibilityListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (War war : WarManager.warsByAttacker.values()) {
            WarManager.updateWarVisibility(war);
        }
    }
}

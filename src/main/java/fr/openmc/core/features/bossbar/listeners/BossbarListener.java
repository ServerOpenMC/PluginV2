package fr.openmc.core.features.bossbar.listeners;

import fr.openmc.core.features.bossbar.BossbarManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BossbarListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BossbarManager.getInstance().addBossBar(event.getPlayer());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BossbarManager.getInstance().removeBossBar(event.getPlayer());
    }
}

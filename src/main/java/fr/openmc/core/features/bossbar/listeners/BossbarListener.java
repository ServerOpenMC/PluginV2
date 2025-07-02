package fr.openmc.core.features.bossbar.listeners;

import fr.openmc.core.features.bossbar.BossbarManager;
import fr.openmc.core.features.bossbar.BossbarsType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static fr.openmc.core.features.bossbar.BossbarManager.bossBarHelp;

public class BossbarListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BossbarManager.addBossBar(BossbarsType.HELP, bossBarHelp, event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BossbarManager.removeBossBar(BossbarsType.HELP, event.getPlayer());
    }
}

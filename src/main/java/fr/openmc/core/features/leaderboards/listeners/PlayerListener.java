package fr.openmc.core.features.leaderboards.listeners;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.leaderboards.Utils.PacketUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

    private final LeaderboardManager manager;

    public PlayerListener(LeaderboardManager manager) {
        this.manager = manager;
    }

    // Quand un joueur rejoint le serveur, on lui envoie le leaderboard seulement s'il est dans le monde du leaderboard.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendLeaderboard(event.getPlayer());
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    // Quand un joueur change de monde, on lui envoie le leaderboard seulement s'il est dans le monde du leaderboard.
    // Important, car minecraft ne gère pas les différents mondes, si on lui envoie un packet d'entité, il l'affichera dans son monde actuel.
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendLeaderboard(event.getPlayer());
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    public void sendLeaderboard(Player player) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        if (player.getWorld().equals(manager.getContributorsHologramLocation().getWorld())) { //Vérifie si le joueur est dans le monde du leaderboard
            protocolManager.sendServerPacket(
                    player,
                    PacketUtils.getTextDisplaySpawnPacket(manager.getContributorsHologramLocation(), 100000)
            );
        }
        if (player.getWorld().equals(manager.getMoneyHologramLocation().getWorld())) { //Vérifie si le joueur est dans le monde du leaderboard
            protocolManager.sendServerPacket(
                    player,
                    PacketUtils.getTextDisplaySpawnPacket(manager.getMoneyHologramLocation(), 100001)
            );
        }
        if (player.getWorld().equals(manager.getVilleMoneyHologramLocation().getWorld())) { //Vérifie si le joueur est dans le monde du leaderboard
            protocolManager.sendServerPacket(
                    player,
                    PacketUtils.getTextDisplaySpawnPacket(manager.getVilleMoneyHologramLocation(), 100002)
            );
        }
        if (player.getWorld().equals(manager.getPlayTimeHologramLocation().getWorld())) { //Vérifie si le joueur est dans le monde du leaderboard
            protocolManager.sendServerPacket(
                    player,
                    PacketUtils.getTextDisplaySpawnPacket(manager.getPlayTimeHologramLocation(), 100003)
            );
        }
    }
}
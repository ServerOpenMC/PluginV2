package fr.openmc.core.features.leaderboards.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.leaderboards.Utils.PacketUtils;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

import static fr.openmc.core.features.leaderboards.LeaderboardManager.*;

public class LeaderboardListener extends PacketAdapter implements Listener {

    private final LeaderboardManager manager;
    private final Chunk contributorsHologramChunk;
    private final Chunk moneyHologramChunk;
    private final Chunk villeMoneyHologramChunk;
    private final Chunk playTimeHologramChunk;

    public LeaderboardListener(LeaderboardManager manager) {
        super(OMCPlugin.getInstance(), PacketType.Play.Server.MAP_CHUNK);
        this.manager = manager;
        contributorsHologramChunk = manager.getContributorsHologramLocation().getChunk();
        moneyHologramChunk = manager.getMoneyHologramLocation().getChunk();
        villeMoneyHologramChunk = manager.getVilleMoneyHologramLocation().getChunk();
        playTimeHologramChunk = manager.getPlayTimeHologramLocation().getChunk();
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

    @Override
    public void onPacketSending(PacketEvent event) {
        var player = event.getPlayer();
        int x = event.getPacket().getIntegers().read(0);
        int z = event.getPacket().getIntegers().read(1);
        if (player.getWorld() == manager.getContributorsHologramLocation().getChunk().getWorld() && x == contributorsHologramChunk.getX() && z == contributorsHologramChunk.getZ()) {
            String text = JSONComponentSerializer.json().serialize(createContributorsTextLeaderboard());
            manager.updateHologram(Collections.singleton(player), text, 100000);
        }
        if (player.getWorld() == manager.getMoneyHologramLocation().getChunk().getWorld() && x == moneyHologramChunk.getX() && z == moneyHologramChunk.getZ()) {
            String text = JSONComponentSerializer.json().serialize(createMoneyTextLeaderboard());
            manager.updateHologram(Collections.singleton(player), text, 100001);
        }
        if (player.getWorld() == manager.getVilleMoneyHologramLocation().getChunk().getWorld() && x == villeMoneyHologramChunk.getX() && z == villeMoneyHologramChunk.getZ()) {
            String text = JSONComponentSerializer.json().serialize(createCityMoneyTextLeaderboard());
            manager.updateHologram(Collections.singleton(player), text, 100002);
        }
        if (player.getWorld() == manager.getPlayTimeHologramLocation().getChunk().getWorld() && x == playTimeHologramChunk.getX() && z == playTimeHologramChunk.getZ()) {
            String text = JSONComponentSerializer.json().serialize(createPlayTimeTextLeaderboard());
            manager.updateHologram(Collections.singleton(player), text, 100003);
        }
    }
}
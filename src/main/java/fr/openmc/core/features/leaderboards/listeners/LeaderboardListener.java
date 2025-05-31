package fr.openmc.core.features.leaderboards.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.leaderboards.utils.PacketUtils;
import lombok.Getter;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class LeaderboardListener extends PacketAdapter implements Listener {

    @Getter
    public static LeaderboardListener instance;
    private LightChunk contributorsHologramChunk;
    private LightChunk moneyHologramChunk;
    private LightChunk villeMoneyHologramChunk;
    private LightChunk playTimeHologramChunk;
    private boolean enabled = false;

    public LeaderboardListener() {
        super(OMCPlugin.getInstance(), PacketType.Play.Server.MAP_CHUNK);
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        reload();
    }

    public void reload() {
        contributorsHologramChunk = LightChunk.fromBukkitChunk(LeaderboardManager.getContributorsHologramLocation().getChunk());
        moneyHologramChunk = LightChunk.fromBukkitChunk(LeaderboardManager.getMoneyHologramLocation().getChunk());
        villeMoneyHologramChunk = LightChunk.fromBukkitChunk(LeaderboardManager.getVilleMoneyHologramLocation().getChunk());
        playTimeHologramChunk = LightChunk.fromBukkitChunk(LeaderboardManager.getPlayTimeHologramLocation().getChunk());
    }

    public void enable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
        enabled = true;
    }

    public void disable() {
        ProtocolLibrary.getProtocolManager().removePacketListener(this);
        enabled = false;
    }

    /** Quand un joueur rejoint le serveur, on lui envoie le leaderboard seulement s'il est dans le monde du leaderboard.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                sendLeaderboard(event.getPlayer());
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    /** Quand un joueur change de monde, on lui envoie le leaderboard seulement s'il est dans le monde du leaderboard.
    * Important, car minecraft ne gère pas les différents mondes, si on lui envoie un packet d'entité, il l'affichera dans son monde actuel.
     */
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (!enabled) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                sendLeaderboard(event.getPlayer());
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    public void sendLeaderboard(Player player) {
        if (player.getWorld().equals(contributorsHologramChunk.world)) { //Vérifie si le joueur est dans le monde du leaderboard
            ((CraftPlayer) player).getHandle().connection.send(PacketUtils.getAddEntityPacket(-610329143, LeaderboardManager.getContributorsHologramLocation()));
        }
        if (player.getWorld().equals(moneyHologramChunk.world)) { //Vérifie si le joueur est dans le monde du leaderboard
            ((CraftPlayer) player).getHandle().connection.send(PacketUtils.getAddEntityPacket(-102388303, LeaderboardManager.getMoneyHologramLocation()));
        }
        if (player.getWorld().equals(villeMoneyHologramChunk.world)) { //Vérifie si le joueur est dans le monde du leaderboard
            ((CraftPlayer) player).getHandle().connection.send(PacketUtils.getAddEntityPacket(-699947630, LeaderboardManager.getVilleMoneyHologramLocation()));
        }
        if (player.getWorld().equals(playTimeHologramChunk.world)) { //Vérifie si le joueur est dans le monde du leaderboard
            ((CraftPlayer) player).getHandle().connection.send(PacketUtils.getAddEntityPacket(-348090140, LeaderboardManager.getPlayTimeHologramLocation()));
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        int x = event.getPacket().getIntegers().read(0);
        int z = event.getPacket().getIntegers().read(1);
        if (player.getWorld() == contributorsHologramChunk.world && x == contributorsHologramChunk.x && z == contributorsHologramChunk.z) {
            LeaderboardManager.updateHologram(Collections.singleton(player), LeaderboardManager.getContributorsHologramMetadataPacket());
        }
        if (player.getWorld() == moneyHologramChunk.world && x == moneyHologramChunk.x && z == moneyHologramChunk.z) {
            LeaderboardManager.updateHologram(Collections.singleton(player), LeaderboardManager.getMoneyHologramMetadataPacket());
        }
        if (player.getWorld() == villeMoneyHologramChunk.world && x == villeMoneyHologramChunk.x && z == villeMoneyHologramChunk.z) {
            LeaderboardManager.updateHologram(Collections.singleton(player), LeaderboardManager.getVilleMoneyHologramMetadataPacket());
        }
        if (player.getWorld() == playTimeHologramChunk.world && x == playTimeHologramChunk.x && z == playTimeHologramChunk.z) {
            LeaderboardManager.updateHologram(Collections.singleton(player), LeaderboardManager.getPlaytimeHologramMetadataPacket());
        }
    }

    public record LightChunk(World world, int x, int z) {

        /**
         * Creates a new LightChunk instance from a Bukkit Chunk.
         *
         * @param chunk The Bukkit Chunk to convert.
         * @return A new LightChunk instance containing the world, X, and Z coordinates of the given chunk.
         */
        public static LightChunk fromBukkitChunk(Chunk chunk) {
            return new LightChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
        }
    }
}

package fr.openmc.core.features.cosmetics.listeners;

import fr.openmc.core.features.cosmetics.CosmeticManager;
import fr.openmc.core.features.cosmetics.bodycostmetics.BodyCosmetic;
import fr.openmc.core.features.cosmetics.bodycostmetics.BodyCosmeticsManager;
import fr.openmc.core.features.cosmetics.bodycostmetics.cosmetics.GoldWings;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CosmeticListener implements Listener {

    private final BodyCosmeticsManager cosmeticManager;

    public CosmeticListener(JavaPlugin plugin, BodyCosmeticsManager cosmeticManager) {
        this.cosmeticManager = cosmeticManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        BodyCosmetic cosmetic = CosmeticManager.getActivatedCosmetic(player.getUniqueId());
        if (cosmetic == null) return;
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        cosmeticManager.spawnPlayerBodyCosmetic(nmsPlayer, cosmetic);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();

        cosmeticManager.removePlayerBodyCosmetic(playerUUID);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        BodyCosmetic cosmetic = CosmeticManager.getActivatedCosmetic(player.getUniqueId());
        if (cosmetic == null) return;
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        cosmeticManager.spawnPlayerBodyCosmetic(nmsPlayer, cosmetic);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        cosmeticManager.updatePlayerBodyCosmeticsViewerList(playerUUID);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        BodyCosmetic cosmetic = CosmeticManager.getActivatedCosmetic(player.getUniqueId());
        if (cosmetic == null) return;
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        cosmeticManager.spawnPlayerBodyCosmetic(nmsPlayer, cosmetic);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        cosmeticManager.removePlayerBodyCosmetic(playerUUID);
    }
}

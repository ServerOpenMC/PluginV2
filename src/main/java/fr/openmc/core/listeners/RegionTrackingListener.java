package fr.openmc.core.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fr.openmc.core.events.RegionEnterEvent;
import fr.openmc.core.events.RegionLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class RegionTrackingListener implements Listener {
    private final Map<UUID, Set<ProtectedRegion>> playerRegions = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if ((from.getBlockX() >> 2) == (to.getBlockX() >> 2)
                && (from.getBlockY() >> 2) == (to.getBlockY() >> 2)
                && (from.getBlockZ() >> 2) == (to.getBlockZ() >> 2))
            return;

        handleTransition(event.getPlayer(), event.getTo());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        handleTransition(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerRegions.remove(event.getPlayer().getUniqueId());
    }

    private void handleTransition(Player player, Location to) {
        if (to == null || to.getWorld() == null) return;

        Set<ProtectedRegion> newRegions = getRegionsAt(to);
        Set<ProtectedRegion> oldRegions = playerRegions.getOrDefault(player.getUniqueId(), Collections.emptySet());

        if (oldRegions.equals(newRegions)) return;

        for (ProtectedRegion region : oldRegions) {
            if (!newRegions.contains(region)) {
                Bukkit.getPluginManager().callEvent(new RegionLeaveEvent(region, player));
            }
        }

        for (ProtectedRegion region : newRegions) {
            if (!oldRegions.contains(region)) {
                Bukkit.getPluginManager().callEvent(new RegionEnterEvent(region, player));
            }
        }

        playerRegions.put(player.getUniqueId(), newRegions);
    }

    private Set<ProtectedRegion> getRegionsAt(Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet applicable = query.getApplicableRegions(BukkitAdapter.adapt(location));

        Set<ProtectedRegion> regions = new HashSet<>();
        for (ProtectedRegion region : applicable) {
            regions.add(region);
        }
        return regions;
    }
}

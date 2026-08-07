package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class ExplodeProtection implements Listener {
    private static final List<EntityType> NATURAL_EXPLOSIVE_ENTITIES = List.of(
            EntityType.CREEPER,
            EntityType.FIREBALL,
            EntityType.SMALL_FIREBALL,
            EntityType.WITHER_SKULL,
            EntityType.WITHER,
            EntityType.END_CRYSTAL,
            EntityType.TNT_MINECART,
            EntityType.DRAGON_FIREBALL,
            EntityType.SULFUR_CUBE
    );

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed tnt) {
            if (tnt.getSource() instanceof Player player) {
                handlePlayerTntExplosion(event, player);
            } else {
                handleNaturalExplosion(event);
            }
            return;
        }

        if (NATURAL_EXPLOSIVE_ENTITIES.contains(entity.getType())) {
            handleNaturalExplosion(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            City blockCity = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());

            return blockCity != null;
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.WITHER || entity.getType() == EntityType.WITHER_SKULL) {
            City city = CityManager.getCityFromChunk(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
            if (city != null) {
                event.setCancelled(true);
            }
        }
    }

    private void handlePlayerTntExplosion(EntityExplodeEvent event, Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        event.blockList().removeIf(block -> {
            City blockCity = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());
            if (blockCity == null) return false;

            return !blockCity.equals(playerCity) && !blockCity.isMember(player);
        });
    }

    private void handleNaturalExplosion(EntityExplodeEvent event) {
        if (!ProtectionsManager.canExplodeNaturally(event.getLocation())) {
            event.setCancelled(true);
            return;
        }

        event.blockList().removeIf(block -> !ProtectionsManager.canExplodeNaturally(block.getLocation()));
    }
}


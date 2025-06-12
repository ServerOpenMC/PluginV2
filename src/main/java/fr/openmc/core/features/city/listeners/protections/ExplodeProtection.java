package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
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
            EntityType.DRAGON_FIREBALL
    );

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed tnt && tnt.getSource() instanceof Player player) {
            City cityz = CityManager.getPlayerCity(player.getUniqueId());

            event.blockList().removeIf(block -> {
                City blockCity = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());
                if (blockCity == null) return false;

                if (blockCity.isMember(player)) return false;
                if (cityz != null) {
                    CityType type1 = blockCity.getType();
                    CityType type2 = cityz.getType();

                    return !(type1 != null && type2 != null && type1.equals(CityType.WAR) && type2.equals(CityType.WAR));
                }
                return true;
            });
            return;
        }

        if (entity instanceof TNTPrimed) {
            event.blockList().removeIf(block -> {
                City city = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());
                return city != null && CityType.PEACE.equals(city.getType());
            });
            return;
        }

        if (NATURAL_EXPLOSIVE_ENTITIES.contains(entity.getType())) {
            event.blockList().removeIf(block -> {
                City city = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());
                return city != null && CityType.PEACE.equals(city.getType());
            });
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            City blockCity = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());

            return blockCity != null && blockCity.getType().equals(CityType.PEACE);
        });
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.WITHER || entity.getType() == EntityType.WITHER_SKULL) {
            City city = CityManager.getCityFromChunk(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
            if (city != null && city.getType().equals(CityType.PEACE)) {
                event.setCancelled(true);
            }
        }
    }
}

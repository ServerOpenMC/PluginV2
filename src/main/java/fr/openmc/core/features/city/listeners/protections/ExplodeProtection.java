package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;

public class ExplodeProtection implements Listener {
    public static final Map<UUID, CityExplosionData> explosionDataMap = new HashMap<>();
    public static final int MAX_TNT_PER_DAY = 2;

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

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed tnt) {
            if (tnt.getSource() instanceof Player player) {
                handlePlayerTntExplosion(event, player);
            } else {
                handleAnonymousTntExplosion(event);
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

            return blockCity != null && blockCity.getType().equals(CityType.PEACE);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.WITHER || entity.getType() == EntityType.WITHER_SKULL) {
            City city = CityManager.getCityFromChunk(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
            if (city != null && city.getType().equals(CityType.PEACE)) {
                event.setCancelled(true);
            }
        }
    }

    private void handlePlayerTntExplosion(EntityExplodeEvent event, Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        Set<UUID> countedCities = new HashSet<>();

        event.blockList().removeIf(block -> {
            City blockCity = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());
            if (blockCity == null) return false;

            if (blockCity.equals(playerCity) || blockCity.isMember(player)) {
                return false;
            }

            if (blockCity.getType() == CityType.WAR && playerCity != null && playerCity.getType() == CityType.WAR) {
                CityExplosionData data = explosionDataMap.computeIfAbsent(blockCity.getUniqueId(), k -> new CityExplosionData());

                if (!data.canExplode(MAX_TNT_PER_DAY)) {
                    return true;
                }

                if (!countedCities.contains(blockCity.getUniqueId())) {
                    countedCities.add(blockCity.getUniqueId());

                    notifyCityMembers(blockCity, playerCity, player);
                }

                return false;
            }

            return true;
        });

        for (UUID cityUUID : countedCities) {
            CityExplosionData data = explosionDataMap.computeIfAbsent(cityUUID, k -> new CityExplosionData());

            data.increment();
        }
    }

    private void notifyCityMembers(City city, City attackerCity, Player attacker) {
        for (UUID memberUUID : city.getMembers()) {
            int currentTnt = explosionDataMap.get(city.getUniqueId()).getExplosions() + 1;
            OfflinePlayer member = CacheOfflinePlayer.getOfflinePlayer(memberUUID);
            if (member.isOnline()) {
                MessagesManager.sendMessage(
                        (Player) member,
                        TranslationManager.translation("feature.city.listeners.tnt_explode",
                                Component.text(attackerCity.getName()).color(NamedTextColor.DARK_RED),
                                Component.text(attacker.getName()).color(NamedTextColor.DARK_RED),
                                Component.text(currentTnt).color(NamedTextColor.RED),
                                Component.text(MAX_TNT_PER_DAY).color(NamedTextColor.RED)
                        ),
		                Prefix.CITY,
                        MessageType.WARNING,
                        false
                );
            }
        }
    }

    private void handleAnonymousTntExplosion(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> !ProtectionsManager.canExplodeNaturally(block.getLocation()));
    }

    private void handleNaturalExplosion(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> !ProtectionsManager.canExplodeNaturally(block.getLocation()));
    }
}


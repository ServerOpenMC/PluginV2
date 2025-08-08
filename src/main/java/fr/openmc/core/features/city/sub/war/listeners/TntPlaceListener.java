package fr.openmc.core.features.city.sub.war.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.listeners.protections.CityExplosionData;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import static fr.openmc.core.features.city.listeners.protections.ExplodeProtection.MAX_TNT_PER_DAY;
import static fr.openmc.core.features.city.listeners.protections.ExplodeProtection.explosionDataMap;

public class TntPlaceListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceTNT(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.TNT) return;

        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        City cityAtLoc = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (cityAtLoc == null) {
            return;
        }

        boolean sameCity = cityAtLoc.isMember(player);

        CityExplosionData data = explosionDataMap.get(cityAtLoc.getUUID());
        int current = (data == null) ? 0 : data.getExplosions();
        if (current >= MAX_TNT_PER_DAY && !sameCity) {
            MessagesManager.sendMessage(
                    player,
                    Component.text("La ville §4" + cityAtLoc.getName() + " §fa atteint sa limite journalière de tnt! §8(§c" + current + "§8/§c" + MAX_TNT_PER_DAY + " tnt journalière§8)"),
                    Prefix.CITY,
                    MessageType.WARNING,
                    false
            );
            ProtectionsManager.verify(event.getPlayer(), event, event.getBlock().getLocation());
            return;
        }

        boolean bothInWar = playerCity != null
                && playerCity.getType() == CityType.WAR
                && cityAtLoc.getType() == CityType.WAR;

        if (sameCity) {
            event.setCancelled(false);
            return;
        }

        if (bothInWar) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                loc.getBlock().setType(Material.AIR);
                TNTPrimed tnt = loc.getWorld().spawn(loc.add(0.5, 0, 0.5), TNTPrimed.class);
                tnt.setSource(player);
                tnt.setFuseTicks(40);
            });
            return;
        }

        event.setCancelled(true);
    }
}

package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GPSTrackerPerk implements Listener {

    private final Map<UUID, City> lastCityMap = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (MayorManager.phaseMayor != 2) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Chunk newChunk = event.getTo().getChunk();
        if (event.getFrom().getChunk().equals(newChunk)) return;

        City newCity = CityManager.getCityFromChunk(
                newChunk.getX(),
                newChunk.getZ()
        );

        City oldCity = lastCityMap.get(uuid);

        if (Objects.equals(oldCity, newCity)) return;

        lastCityMap.put(uuid, newCity);

        if (oldCity != null && hasGpsTrackerPerk(oldCity) && !oldCity.isMember(player)) {
            removeGlowing(player);
        }

        if (newCity != null && hasGpsTrackerPerk(newCity) && !newCity.isMember(player)) {
            applyGlowing(player);
            MessagesManager.sendMessage(
                    player,
                    Component.text("§cVous venez d'entrer dans la ville §e" + newCity.getName() + "§c qui dispose du §e§lGPS Tracker§c ! Soyez sur vos gardes."),
                    Prefix.MAYOR,
                    MessageType.INFO,
                    false
            );
        } else {
            removeGlowing(player);
        }
    }

    private boolean hasGpsTrackerPerk(City city) {
        Mayor mayor = city.getMayor();
        return PerkManager.hasPerk(mayor, Perks.GPS_TRACKER.getId());
    }

    private void applyGlowing(Player player) {
        player.setGlowing(true);
    }

    private void removeGlowing(Player player) {
        player.setGlowing(false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        lastCityMap.remove(player.getUniqueId());
        removeGlowing(player);
    }
}

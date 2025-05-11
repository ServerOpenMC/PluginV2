package fr.openmc.core.features.city.mayor.perks.basic;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class GPSTrackerPerk implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (MayorManager.getInstance().phaseMayor != 2) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // ça sert a rien de lancer ça si on change pas de chunk
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;

        City playerCity = CityManager.getPlayerCity(uuid);
        if (playerCity == null) return;


        City currentCity = CityManager.getCityFromChunk(
                event.getTo().getChunk().getX(),
                event.getTo().getChunk().getZ()
        );
        if (currentCity == null) return;

        if (!PerkManager.hasPerk(currentCity.getMayor(), Perks.GPS_TRACKER.getId())) return;

        player.removePotionEffect(PotionEffectType.GLOWING);

        if (currentCity != null) {
            if (!currentCity.equals(playerCity)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 0, false, false));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        player.removePotionEffect(PotionEffectType.GLOWING);
    }
}

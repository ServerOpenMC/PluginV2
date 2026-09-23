package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.perks.PerkUtils;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.homes.events.HomeTpEvent;
import fr.openmc.core.features.homes.models.Home;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class RagePerk implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (OMCRegistry.CITY_FEATURES.MAYOR.phaseMayor != 2) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // ça sert à rien de lancer ça si on ne change pas de chunk
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;

        City playerCity = City.ofPlayer(uuid);
        if (playerCity == null) return;

        if (!PerkUtils.hasPerk(playerCity.getMayor(), Perks.FOU_DE_RAGE.getId())) return;

        City currentCity = City.of(event.getTo());

        updateEffect(currentCity, player);
    }

    @EventHandler
    public void onTpMove(HomeTpEvent event) {
        if (OMCRegistry.CITY_FEATURES.MAYOR.phaseMayor != 2) return;

        Player player = event.getPlayer();

        City playerCity = City.ofPlayer(player.getUniqueId());
        if (playerCity == null) return;

        if (!PerkUtils.hasPerk(playerCity.getMayor(), Perks.FOU_DE_RAGE.getId())) return;

        Home home = event.getHome();

        City currentCity = City.of(home.getLocation());

        updateEffect(currentCity, player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
    }

    public static void updateEffect(City currentCity, Player player) {
        City playerCity = City.ofPlayer(player);
        if (playerCity == null) return;

        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.RESISTANCE);

        if (currentCity == null) return;

        if (currentCity.equals(playerCity)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0, false, false));
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 0, false, false));
        }
    }
}

package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
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
        if (MayorManager.phaseMayor != 2) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // ça sert à rien de lancer ça si on ne change pas de chunk
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;

        City playerCity = CityManager.getPlayerCity(uuid);
        if (playerCity == null) return;

        if (!PerkManager.hasPerk(playerCity.getMayor(), Perks.FOU_DE_RAGE.getId())) return;

        City currentCity = CityManager.getCityFromChunk(
                event.getTo().getChunk().getX(),
                event.getTo().getChunk().getZ()
        );

        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.RESISTANCE);

        if (currentCity == null) return;

        if (currentCity.equals(playerCity)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0, false, false));
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 0, false, false));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
    }
}

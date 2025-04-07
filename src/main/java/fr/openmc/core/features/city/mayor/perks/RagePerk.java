package fr.openmc.core.features.city.mayor.perks;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RagePerk implements Listener {
    private final Map<UUID, String> lastCityIn = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (MayorManager.getInstance().phaseMayor != 2) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // ça sert a rien de lancer ça si on change pas de chunk
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;

        City playerCity = CityManager.getPlayerCity(uuid);
        if (playerCity == null) return;

        // si ville a pas le perk 1 soit FOU_DE_RAGE
        if (!PerkManager.hasPerk(playerCity.getMayor(), 1)) return;


        City currentCity = CityManager.getCityFromChunk(
                event.getTo().getChunk().getX(),
                event.getTo().getChunk().getZ()
        );

        String currentCityName = currentCity != null ? currentCity.getName() : null;
        String lastCityName = lastCityIn.get(uuid);

        if (Objects.equals(currentCityName, lastCityName)) return;

        lastCityIn.put(uuid, currentCityName);

        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.RESISTANCE);

        if (currentCity != null) {
            if (currentCity.equals(playerCity)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 0, false, false));
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0, false, false));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.RESISTANCE);


        UUID uuid = player.getUniqueId();
        lastCityIn.remove(uuid);
    }
}

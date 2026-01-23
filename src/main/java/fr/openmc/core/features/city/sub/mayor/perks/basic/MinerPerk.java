package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MinerPerk implements Listener {

    /**
     * Update the player's effects based on the current phase and their city perks.
     *
     * @param player The player to update.
     */
    public static void updatePlayerEffects(Player player) {
        int phase = MayorManager.phaseMayor;

        if (phase == 2) {
            City playerCity = CityManager.getPlayerCity(player.getUniqueId());
            if (playerCity == null) return;

            if (!PerkManager.hasPerk(playerCity.getMayor(), Perks.MINER.getId())) return;

            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 0, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.HASTE);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerEffects(player);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            updatePlayerEffects(player);
        }, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.HASTE);
    }

    @EventHandler
    public void onMilkEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() == Material.MILK_BUCKET)  {
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                    updatePlayerEffects(player), 1L);
        }
    }

    @EventHandler
    public void onDreamEntrered(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isDreamWorld(event.getTo())) return;
        if (DreamUtils.isDreamWorld(event.getFrom())) return;

        player.removePotionEffect(PotionEffectType.HASTE);
    }

    @EventHandler
    public void onDreamLeave(PlayerTeleportEvent event) {
        if (!DreamUtils.isDreamWorld(event.getFrom())) return;
        if (DreamUtils.isDreamWorld(event.getTo())) return;

        updatePlayerEffects(event.getPlayer());
    }
}

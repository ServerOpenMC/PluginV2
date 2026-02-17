package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.events.ArmorEquipEvent;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.ArmorType;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AywenCapListener implements Listener {

    @EventHandler
    public void onPlayerEquip(ArmorEquipEvent event) {
        if (event.getType() == null || !event.getType().equals(ArmorType.HELMET)) return;
        Player player = event.getPlayer();

        ItemStack aywenCap = CustomItemRegistry.getByName("omc_items:aywen_cap").getBest();

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            ItemStack helmetNow = player.getInventory().getHelmet();
            boolean hasCapNow = ItemUtils.isSimilar(aywenCap, helmetNow);
            //TODO: Ajouter un CustomItemEquipable avec un systeme de gestion d'effet compatible CustomItem et DreamItem.

            if (hasCapNow && !player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            } else if (!ItemUtils.isSimilar(DreamItemRegistry.getByName("omc_dream:dream_helmet").getBest(), helmetNow)) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }

        }, 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();
        if (ItemUtils.isSimilar(CustomItemRegistry.getByName("omc_items:aywen_cap").getBest(), helmet)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    @EventHandler
    public void onDreamTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        boolean entering = !DreamUtils.isDreamWorld(event.getFrom()) && DreamUtils.isDreamWorld(event.getTo());
        boolean leaving = DreamUtils.isDreamWorld(event.getFrom()) && !DreamUtils.isDreamWorld(event.getTo());

        if (!entering && !leaving) return;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> recalcEffects(player), 20L);
    }

    private void recalcEffects(Player player) {
        ItemStack equipped = player.getInventory().getHelmet();
        ItemStack aywenCap = CustomItemRegistry.getByName("omc_items:aywen_cap").getBest();

        if (ItemUtils.isSimilar(aywenCap, equipped)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        } else if (!ItemUtils.isSimilar(DreamItemRegistry.getByName("omc_dream:dream_helmet").getBest(), equipped)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}

package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.events.ArmorEquipEvent;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ArmorType;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AywenCapListener implements Listener {

    @EventHandler
    public void onPlayerEquip(ArmorEquipEvent event) {
        if (event.getType() == null || !event.getType().equals(ArmorType.HELMET)) return;
        Player player = event.getPlayer();

        ItemStack aywenCap = CustomItemRegistry.getByName("omc_items:aywen_cap").getBest();

        boolean wasWearingCap = ItemUtils.isSimilar(aywenCap, event.getOldArmorPiece());
        boolean isWearingCap = ItemUtils.isSimilar(aywenCap, event.getNewArmorPiece());

        player.getServer().getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (isWearingCap && !player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            } else if (!isWearingCap && wasWearingCap) {
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
}

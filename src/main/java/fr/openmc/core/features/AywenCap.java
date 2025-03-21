package fr.openmc.core.features;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AywenCap implements Listener {
    public AywenCap() {
        OMCPlugin.registerEvents(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (hasAywenCap(player)) {
            giveNightvision(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (hasAywenCap(player)) {
            removeNightvision(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                if (hasAywenCap(player)) {
                    giveNightvision(player);
                } else {
                    removeNightvision(player);
                }
            }, 1L);
        }
    }

    private void giveNightvision(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));
    }

    private void removeNightvision(Player player) {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public boolean isAywenCap(ItemStack item) {
        if (item == null) return false;
        CustomStack cs = CustomStack.byItemStack(item);
        if (cs == null) return false;

        return cs.getNamespacedID().equals("aywen:cap");
    }

    public boolean hasAywenCap(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        return isAywenCap(helmet);
    }
}
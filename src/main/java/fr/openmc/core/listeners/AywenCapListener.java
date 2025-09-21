package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AywenCapListener implements Listener {

    private void updateEffect(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (ItemUtils.isSimilar(helmet, CustomItemRegistry.getByName("omc_items:aywen_cap").getBest())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        player.getServer().getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> updateEffect(player), 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateEffect(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
}

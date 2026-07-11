package fr.openmc.core.features.profile.listeners;

import fr.openmc.core.features.profile.menu.ProfileMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ProfileInteractionListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player viewer = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND || !viewer.isSneaking()) return;
        if (!(event.getRightClicked() instanceof Player target)) {
            return;
        }

        event.setCancelled(true);
        new ProfileMenu(viewer, target).open();
    }
}

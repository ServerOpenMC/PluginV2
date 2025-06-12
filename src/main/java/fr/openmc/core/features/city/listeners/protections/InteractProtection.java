package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.mascots.MascotUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractProtection implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        ItemStack inHand = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_AIR && inHand != null && inHand.getType().isEdible()) {
            return;
        }

        if (event.getClickedBlock() == null) return;

        Location loc = event.getClickedBlock().getLocation();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (inHand != null && inHand.getType().isEdible()) {
                Block clicked = event.getClickedBlock();
                Material type = clicked.getType();

                if (!type.isInteractable()) return;
            }

            ProtectionsManager.verify(player, event, loc);
        }
    }


    @EventHandler
    void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        Entity rightClicked = event.getRightClicked();

        if (rightClicked instanceof Player) return;
        if (MascotUtils.isMascot(rightClicked)) return;

        ProtectionsManager.verify(event.getPlayer(), event, rightClicked.getLocation());
    }
}

package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
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
        
        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack inHand = event.getItem();

        if (inHand != null) {
            Material type = inHand.getType();

            if (type == Material.TNT) return;

            if (type.isBlock()) return;

            if (type.isEdible() && event.getAction() == Action.RIGHT_CLICK_AIR) return;
        }
	    
	    Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Location loc = clickedBlock.getLocation();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (inHand != null && inHand.getType().isEdible()) {
                Material type = clickedBlock.getType();

                if (!type.isInteractable()) return;
            }
            
            City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
            if (city == null) return;
            
            if (city.isMember(player)) {
                if (clickedBlock.getType().name().endsWith("SHULKER_BOX")) return;
                if (clickedBlock.getType().name().endsWith("CHEST") || clickedBlock.getType().name().endsWith("BARREL")) {
                    ProtectionsManager.checkPermissions(player, event, city, CPermission.OPEN_CHEST);
                } else {
                    ProtectionsManager.checkPermissions(player, event, city, CPermission.INTERACT);
                }
                
            } else {
                ProtectionsManager.checkCity(player, event, city);
            }
            
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

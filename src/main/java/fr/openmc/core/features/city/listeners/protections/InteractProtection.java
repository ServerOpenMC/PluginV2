package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
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
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractProtection implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.PHYSICAL) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Location location = clickedBlock.getLocation();

        ItemStack inHand = event.getItem();
        Material itemType = inHand != null ? inHand.getType() : Material.AIR;

        boolean isMinecart = isMinecart(itemType);
        boolean isTnt = itemType == Material.TNT;

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
                    ProtectionsManager.checkPermissions(player, event, city, CityPermission.OPEN_CHEST);
                } else {
                    ProtectionsManager.checkPermissions(player, event, city, CityPermission.INTERACT);
                }

            } else {
                ProtectionsManager.checkCity(player, event, city);
            }

        }

        if (!isMinecart) return;
        if (isTnt) return;

        ProtectionsManager.verify(player, event, location);
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        Entity rightClicked = event.getRightClicked();
        if (rightClicked instanceof Player) return;
        if (!(rightClicked instanceof ItemFrame)) return;

        if (MascotUtils.canBeAMascot(rightClicked)) return;

        ProtectionsManager.verify(event.getPlayer(), event, rightClicked.getLocation());
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getBlockClicked().getLocation());
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getBlockClicked().getLocation());
    }

    private boolean isMinecart(Material type) {
        return switch (type) {
            case MINECART, CHEST_MINECART, FURNACE_MINECART, HOPPER_MINECART, TNT_MINECART, COMMAND_BLOCK_MINECART ->
                    true;
            default -> false;
        };
    }
}

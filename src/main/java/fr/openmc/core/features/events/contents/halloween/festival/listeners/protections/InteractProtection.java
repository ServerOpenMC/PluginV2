package fr.openmc.core.features.events.contents.halloween.festival.listeners.protections;

import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import fr.openmc.core.features.shops.managers.ShopManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

import java.util.HashSet;
import java.util.Set;

// dupli
public class InteractProtection implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.PHYSICAL) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Location location = clickedBlock.getLocation();

        ItemStack inHand = event.getItem();
        Material itemType = inHand != null ? inHand.getType() : Material.AIR;

        boolean isMinecart = isMinecart(itemType);
        boolean isTnt = itemType == Material.TNT;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (inHand != null && inHand.getType().isEdible()) {
                Material type = clickedBlock.getType();

                if (!type.isInteractable()) return;
            }
            
            if (ShopManager.getShopAt(location) != null) {
                if (clickedBlock.getState() instanceof Barrel) return;
                event.setCancelled(true);
                return;
            }

            if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
                return;

            if (!event.getPlayer().isOp())
                event.setCancelled(true);
        }

        if (!isMinecart) return;
        if (isTnt) return;

        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    private final Set<EntityType> INTERACTION_REFUSED = new HashSet<>(Set.of(
            EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.SULFUR_CUBE
    ));

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        Entity rightClicked = event.getRightClicked();
        if (rightClicked instanceof Player) return;
        
        if (!INTERACTION_REFUSED.contains(rightClicked.getType())) return;

        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    private boolean isMinecart(Material type) {
        return switch (type) {
            case MINECART, CHEST_MINECART, FURNACE_MINECART, HOPPER_MINECART, TNT_MINECART, COMMAND_BLOCK_MINECART ->
                    true;
            default -> false;
        };
    }
}

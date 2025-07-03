package fr.openmc.core.features.city.listeners.protections;

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
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Location location = clickedBlock.getLocation();
        Material clickedType = clickedBlock.getType();

        ItemStack inHand = event.getItem();
        Material itemType = inHand != null ? inHand.getType() : Material.AIR;

        boolean isMinecart = isMinecart(itemType);
        boolean isTnt = itemType == Material.TNT;

        if (!clickedType.isInteractable() && !isMinecart) return;

        if (!isMinecart) return;
        if (isTnt) return;

        ProtectionsManager.verify(player, event, location);
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        if (entity instanceof Player) return;
        if (!(entity instanceof ItemFrame)) return;
        if (MascotUtils.isMascot(entity)) return;

        ProtectionsManager.verify(event.getPlayer(), event, entity.getLocation());
    }

    private boolean isMinecart(Material type) {
        return switch (type) {
            case MINECART, CHEST_MINECART, FURNACE_MINECART, HOPPER_MINECART, TNT_MINECART, COMMAND_BLOCK_MINECART ->
                    true;
            default -> false;
        };
    }
}

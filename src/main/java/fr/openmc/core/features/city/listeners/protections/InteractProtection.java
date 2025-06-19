package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
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
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Material clickedType = clickedBlock.getType();

        if (!clickedType.isInteractable()) return;

        ItemStack inHand = event.getItem();
        if (inHand != null && inHand.getType() == Material.TNT) return;

        ProtectionsManager.verify(player, event, clickedBlock.getLocation());
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        if (entity instanceof Player) return;
        if (entity instanceof ItemFrame == false) return;
        if (MascotUtils.isMascot(entity)) return;

        ProtectionsManager.verify(event.getPlayer(), event, entity.getLocation());
    }
}

package fr.openmc.core.features.contest.listeners;

import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

public class ContestIntractEvents implements Listener {
    //TODO: faire si on a pas item adder
    @EventHandler
    private void onFurnitureInteractEvent(FurnitureInteractEvent furniture) {
        if (furniture.getNamespacedID().equals("contest:borne")) {
            furniture.getPlayer().playSound(furniture.getPlayer().getLocation(), Sound.BLOCK_BARREL_OPEN, 1.0F, 0.7F);
            Bukkit.dispatchCommand(furniture.getPlayer(), "OMCPlugin:contest");
        }
    }
}

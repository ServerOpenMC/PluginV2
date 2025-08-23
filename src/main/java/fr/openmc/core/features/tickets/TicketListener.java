package fr.openmc.core.features.tickets;

import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import fr.openmc.core.features.tickets.menus.MachineBallsMenu;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TicketListener implements Listener {

    @EventHandler
    public void onMachineBallsInteraction(FurnitureInteractEvent furniture) {
        if (furniture.getNamespacedID().equals("omc_blocks:ball_machine")) {
            furniture.getPlayer().playSound(net.kyori.adventure.sound.Sound.sound(Key.key("minecraft", "block.barrel.open"), Sound.Source.BLOCK, 1f, 1f));
            new MachineBallsMenu(furniture.getPlayer()).open();
        }
    }

}

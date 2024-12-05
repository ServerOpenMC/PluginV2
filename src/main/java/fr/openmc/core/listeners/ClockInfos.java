package fr.openmc.core.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

/*
* Repris de src/main/java/fr/communaywen/core/clockinfos/tasks/CompassClockTask.java
* Travail original par Fnafgameur
*/
public class ClockInfos implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        if (inv.getItemInMainHand().getType() != Material.CLOCK) return;

        long daysPassed = player.getWorld().getFullTime() / 24000;
        long currentTime = player.getWorld().getTime();
        long time = currentTime + (daysPassed * 24000);
        long hours = (time / 1000 + 6) % 24;
        long minutes = (time % 1000) * 60 / 1000;

        String minutesAsText = String.format("%02d", minutes);
        String hoursAsText = String.format("%02d", hours);

        // J12 03h49
        player.sendActionBar(Component.text("J"+daysPassed+" "+hoursAsText+"h"+minutesAsText));
    }
}

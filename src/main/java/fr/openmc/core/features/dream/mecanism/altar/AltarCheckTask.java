package fr.openmc.core.features.dream.mecanism.altar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class AltarCheckTask extends BukkitRunnable {

    private static final double MAX_DISTANCE = 30.0;

    @Override
    public void run() {
        Iterator<Map.Entry<Location, UUID>> iterator = AltarManager.boundPlayers.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Location, UUID> entry = iterator.next();
            Location altarLoc = entry.getKey();
            UUID uuid = entry.getValue();

            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                AltarManager.unbind(altarLoc);
                iterator.remove();
                continue;
            }

            if (!player.getWorld().equals(altarLoc.getWorld())) {
                AltarManager.unbind(altarLoc);
                iterator.remove();
                continue;
            }

            if (player.getLocation().distanceSquared(altarLoc) > MAX_DISTANCE * MAX_DISTANCE) {
                AltarManager.unbind(altarLoc);
                iterator.remove();
            }
        }
    }
}

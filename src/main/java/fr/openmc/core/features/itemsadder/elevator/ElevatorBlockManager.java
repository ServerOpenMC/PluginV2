package fr.openmc.core.features.itemsadder.elevator;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCRegistry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.*;

public class ElevatorBlockManager {

    // Map<(X,Z), Set<Y>>
    public Map<Vector2i, Set<Integer>> elevatorsPerColumn = new HashMap<>();

    //TODO pouvoir load les elevator quand reboot :
    // - solution 1, charger quand le joueur l'utilise ( bcp moins gourmant mais peut avoir problème )
    // - solution 2, regarder pour chacun des elevator sur la map et load colonne par colonne ( évite les prolème mais peut être gourmant )
    // - solution 3, changer le système de check pour monter ou descendre

    public boolean isOnTop(Player player) {
        Location playerLocation = player.getLocation();
        Block blockUnderPlayer = playerLocation.getBlock().getRelative(0, -1, 0);

        if (blockUnderPlayer.isEmpty()) return false;

        return CustomBlock.byAlreadyPlaced(blockUnderPlayer)
                .equals(OMCRegistry.CUSTOM_ITEMS.ELEVATOR.getCustomBlock());
    }

    public void addToColumn(@NotNull Location location) {
        elevatorsPerColumn.computeIfAbsent(
                keyOf(location),
                k -> new HashSet<>())
                .add(location.getBlockY());
    }

    public void removeToColumn(@NotNull Location location) {
        elevatorsPerColumn.computeIfPresent(keyOf(location), (k, set) -> {
            set.remove(location.getBlockY());
            return set.isEmpty() ? null : set;
        });
    }

    public Location getNextTop(Player player) {
        Location loc = player.getLocation().clone();

        int playerY = loc.getBlockY();

        int y = elevatorsPerColumn.get(keyOf(loc))
                .stream()
                .filter(v -> v > playerY)
                .min(Comparator.comparingInt(v -> Math.abs(v - playerY)))
                .orElse(playerY);

        loc.setY(y);

        return loc;
    }

    public Location getNextDown(Player player) {
        Location loc = player.getLocation().clone();

        int playerY = loc.getBlockY();

        int y = elevatorsPerColumn.get(keyOf(loc))
                .stream()
                .filter(v -> v < playerY)
                .max(Comparator.comparingInt(v -> Math.abs(v - playerY)))
                .orElse(playerY);

        if (y != playerY)
            loc.setY(y);

        return loc;
    }

    public Vector2i keyOf(@NotNull Location location) {
        return new Vector2i(location.getBlockX(), location.getBlockZ());
    }

}

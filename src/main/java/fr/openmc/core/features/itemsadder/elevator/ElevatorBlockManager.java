package fr.openmc.core.features.itemsadder.elevator;

import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.*;

public class ElevatorBlockManager {

    // Map<(X,Z), Set<Y>>
    public Map<Vector2i, Set<Integer>> elevatorsPerColumn = new HashMap<>();

    /**
     * Know if the player is standing on top of an elevator
     */
    public boolean isOnTop(Player player) {
        Location playerLocation = player.getLocation();
        Block blockUnderPlayer = playerLocation.getBlock().getRelative(0, -1, 0);

        if (blockUnderPlayer.isEmpty()) return false;

        CustomBlock cBlock = CustomBlock.byAlreadyPlaced(blockUnderPlayer);

        if (cBlock == null) return false;

        return cBlock.getNamespacedID().contains("omc_elevator");
    }

    /**
     * Search for elevator to add in the actual column
     */
    private void scanColumn(Location loc) {
        Set<Integer> elevators = new HashSet<>();

        int minY = loc.getWorld().getMinHeight();
        int maxY = loc.getWorld().getMaxHeight();

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        for (int y = minY; y < maxY; y++) {
            Block block = loc.getWorld().getBlockAt(x, y, z);

            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);

            if (customBlock == null)
                continue;

            if (customBlock.getNamespacedID().contains("omc_elevator")) {
                elevators.add(y);
            }
        }

        if (elevators.isEmpty()) return;

        elevatorsPerColumn.put(keyOf(loc), elevators);
    }

    /**
     * Add an elevator to a column
     */
    public void addToColumn(@NotNull Location location) {

        if (!elevatorsPerColumn.containsKey(keyOf(location)))
            scanColumn(location);

        elevatorsPerColumn.computeIfAbsent(
                keyOf(location),
                k -> new HashSet<>())
                .add(location.getBlockY());
    }

    /**
     * Remove an elevator to a column
     */
    public void removeToColumn(@NotNull Location location) {

        if (!elevatorsPerColumn.containsKey(keyOf(location)))
            scanColumn(location);

        elevatorsPerColumn.computeIfPresent(keyOf(location), (k, set) -> {
            set.remove(location.getBlockY());
            return set.isEmpty() ? null : set;
        });
    }

    /**
     * Get the next elevator above the player if there's one
     */
    public Location getNextTop(Player player) {
        Location loc = player.getLocation().clone();

        if (!elevatorsPerColumn.containsKey(keyOf(loc)))
            scanColumn(loc);

        int playerY = loc.getBlockY();

        Set<Integer> elevators = elevatorsPerColumn.get(keyOf(loc));

        if (elevators == null) return loc;

        int y = elevators
                .stream()
                .filter(v -> v > playerY)
                .min(Integer::compareTo)
                .orElse(playerY);

        if (y != playerY)
            loc.setY(y + 1);

        return loc;
    }

    /**
     * Get the next elevator below the player if there's one
     */
    public Location getNextDown(Player player) {
        Location loc = player.getLocation().clone();

        if (!elevatorsPerColumn.containsKey(keyOf(loc)))
            scanColumn(loc);

        int playerY = loc.getBlockY();

        Set<Integer> elevators = elevatorsPerColumn.get(keyOf(loc));

        if (elevators == null) return loc;

        int y = elevators
                .stream()
                .filter(v -> v < playerY - 1)
                .max(Integer::compareTo)
                .orElse(playerY);

        if (y != playerY)
            loc.setY(y + 1);

        return loc;
    }

    /**
     * Convert a location to a Vector2i(X,Z), used as a key for the HashMap
     */
    public Vector2i keyOf(@NotNull Location location) {
        return new Vector2i(location.getBlockX(), location.getBlockZ());
    }

}

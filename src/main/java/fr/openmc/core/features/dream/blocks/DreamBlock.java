package fr.openmc.core.features.dream.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record DreamBlock(String type, Location location) {
    public static DreamBlock fromString(String s) {
        String[] parts = s.split(",");
        if (parts.length != 5) return null;
        World w = Bukkit.getWorld(parts[1]);
        if (w == null) return null;
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);
        int z = Integer.parseInt(parts[4]);
        return new DreamBlock(parts[0], new Location(w, x, y, z));
    }

    @Override
    public String toString() {
        return type + "," +
                location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }
}

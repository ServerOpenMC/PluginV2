package fr.openmc.core.features.homes;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class Home {
    private final UUID player;
    @Setter private String name;
    private final Location location;

    public Home(UUID player, String name, Location location) {
        this.player = player;
        this.name = name;
        this.location = location;
    }

    public String serializeLocation() {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    private Location deserializeLocation(String serializedLocation) {
        String[] location = serializedLocation.split(",");
        return new Location(
                Bukkit.getWorld(location[0]),
                Double.parseDouble(location[1]),
                Double.parseDouble(location[2]),
                Double.parseDouble(location[3]),
                Float.parseFloat(location[4]),
                Float.parseFloat(location[5])
        );
    }
}

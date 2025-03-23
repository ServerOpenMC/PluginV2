package fr.openmc.core.features.homes;

import fr.openmc.core.features.homes.utils.HomeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

@Getter
public class Home {

    private final UUID owner;
    @Setter private String name;
    @Setter private Location location;
    @Setter private HomeIcons icon;

    public Home(UUID owner, String name, Location location, HomeIcons icon) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.icon = icon;
    }

    public String serializeLocation() {
        return location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    public static Location deserializeLocation(String locString) {
        String[] loc = locString.split(",");
        return new Location(
                org.bukkit.Bukkit.getWorld(loc[0]),
                Double.parseDouble(loc[1]),
                Double.parseDouble(loc[2]),
                Double.parseDouble(loc[3]),
                Float.parseFloat(loc[4]),
                Float.parseFloat(loc[5])
        );
    }

    public ItemStack getIconItem() {
        ItemStack item = HomeUtil.getHomeIconItem(this);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a" + name);
        item.setLore(List.of(
                "§6Position:",
                "§6  W: §e" + location.getWorld().getName(),
                "§6  X: §e" + location.getBlockX(),
                "§6  Y: §e" + location.getBlockY(),
                "§6  Z: §e" + location.getBlockZ()
        ));
        item.setItemMeta(meta);
        return item;
    }
}

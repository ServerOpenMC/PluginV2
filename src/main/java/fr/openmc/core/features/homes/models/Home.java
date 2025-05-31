package fr.openmc.core.features.homes.models;

import fr.openmc.core.features.homes.HomeIcons;
import fr.openmc.core.features.homes.utils.HomeUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;
import java.util.UUID;

@Getter
@DatabaseTable(tableName = "homes")
public class Home {

    @DatabaseField(id = true)
    private UUID owner;
    @Setter
    @DatabaseField(canBeNull = false)
    private String name;
    @Setter
    @DatabaseField(canBeNull = false)
    private HomeIcons icon;

    // Location
    @DatabaseField(canBeNull = false)
    private String world;
    @DatabaseField(canBeNull = false)
    private double x;
    @DatabaseField(canBeNull = false)
    private double y;
    @DatabaseField(canBeNull = false)
    private double z;
    @DatabaseField(canBeNull = false)
    private float yaw;
    @DatabaseField(canBeNull = false)
    private float pitch;

    Home() {
        // required for ORMLite
    }

    public Home(UUID owner, String name, Location location, HomeIcons icon) {
        this.owner = owner;
        this.name = name;
        setLocation(location);
        this.icon = icon;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public void setLocation(Location location) {
        world = location.getWorld().getName();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    public String serializeLocation() {
        Location location = getLocation();
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
                Float.parseFloat(loc[5]));
    }

    public ItemStack getIconItem() {
        ItemStack item = HomeUtil.getHomeIconItem(this);
        ItemMeta meta = item.getItemMeta();
        Location location = getLocation();
        meta.displayName(Component.text("§a" + name));
        item.lore(List.of(
                Component.text("§6Position:"),
                Component.text("§6  W: §e" + location.getWorld().getName()),
                Component.text("§6  X: §e" + location.getBlockX()),
                Component.text("§6  Y: §e" + location.getBlockY()),
                Component.text("§6  Z: §e" + location.getBlockZ())));
        item.setItemMeta(meta);
        return item;
    }
}

package fr.openmc.core.features.homes.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.icons.LegacyHomeIcon;
import fr.openmc.core.features.homes.utils.HomeUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

@Getter
@DatabaseTable(tableName = "homes")
public class Home {

    @DatabaseField(id = true, canBeNull = false, unique = true, columnName = "home_uuid")
    private UUID uniqueId;
    @DatabaseField(canBeNull = false)
    private UUID owner;
    @Setter
    @DatabaseField(canBeNull = false)
    private String name;
    @Setter
    @DatabaseField(canBeNull = false)
    private String iconId;

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

    public Home(UUID uniqueId, UUID owner, String name, Location location, HomeIcon icon) {
        this.uniqueId = uniqueId;
        this.owner = owner;
        this.name = name;
        setLocation(location);
        this.iconId = icon.getSaveId();
    }

    public Home(UUID uniqueId, UUID owner, String name, Location location, LegacyHomeIcon legacyIcon) {
        this(uniqueId, owner, name, location, HomeIconRegistry.fromLegacyHomeIcon(legacyIcon));
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public void setLocation(Location location) {
        world = location.getWorld().getName();
        x = (double) Math.round(location.getX() * 10d) / 10d;
        y = (double) Math.round(location.getY() * 10d) / 10d;
        z = (double) Math.round(location.getZ() * 10d) / 10d;
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    public String serializeLocation() {
        Location location = getLocation();
        return location.getWorld().getName() + "," +
                (double) Math.round(location.getX() * 10d) / 10d + "," +
                (double) Math.round(location.getY() * 10d) / 10d + "," +
                (double) Math.round(location.getZ() * 10d) / 10d + "," +
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
        ItemStack item = getIcon().getItemStack().clone();
        ItemMeta meta = item.getItemMeta();
        Location location = getLocation();
        meta.displayName(Component.text("§a" + name));
        item.lore(List.of(
                Component.text("§6Position:"),
                Component.text("§6  W: §e" + location.getWorld().getName()),
                Component.text("§6  X: §e" + (double) Math.round(location.getX() * 10d) / 10d),
                Component.text("§6  Y: §e" + (double) Math.round(location.getY() * 10d) / 10d),
                Component.text("§6  Z: §e" + (double) Math.round(location.getZ() * 10d) / 10d)));
        item.setItemMeta(meta);
        return item;
    }

    @Deprecated
    public LegacyHomeIcon getLegacyIcon() {
        return HomeIconRegistry.toLegacyHomeIcon(getIcon());
    }

    @Deprecated
    public void setLegacyIcon(LegacyHomeIcon legacyIcon) {
        this.iconId = HomeIconRegistry.fromLegacyHomeIcon(legacyIcon).getSaveId();
    }

    public String getIconSaveId() {
        return iconId;
    }

    @Override
    public String toString() {
        return "Home{" +
                "uniqueId=" + uniqueId +
                ", owner=" + owner +
                ", name='" + name + '\'' +
                ", location=" + serializeLocation() +
                ", icon=" + iconId +
                '}';
    }

    public HomeIcon getIcon() {
        if (iconId == null || iconId.isEmpty())
            return HomeIconRegistry.getDefaultIcon();

        HomeIcon icon = HomeIconRegistry.getIcon(iconId);
        if (icon != null)
            return icon;

        try {
            LegacyHomeIcon legacyIcon = LegacyHomeIcon.valueOf(iconId.toUpperCase());
            return HomeIconRegistry.fromLegacyHomeIcon(legacyIcon);
        } catch (IllegalArgumentException e) {
            return HomeUtil.mapLegacyCustomId(iconId);
        }
    }

    public void setIcon(HomeIcon icon) {
        this.iconId = icon.getSaveId();
    }
}

package fr.openmc.core.features.homes;

import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.icons.OldHomeIcon;
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
    @Setter private HomeIcon icon;

    public Home(UUID owner, String name, Location location, HomeIcon icon) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.icon = icon != null ? icon : HomeIconRegistry.getDefaultIcon();
    }

    public Home(UUID owner, String name, Location location, OldHomeIcon legacyIcon) {
        this(owner, name, location, HomeIconRegistry.fromLegacyHomeIcon(legacyIcon));
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
        ItemStack item = icon.getItemStack().clone();
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

    @Deprecated
    public OldHomeIcon getLegacyIcon() {
        return HomeIconRegistry.toLegacyHomeIcon(this.icon);
    }

    @Deprecated
    public void setLegacyIcon(OldHomeIcon legacyIcon) {
        this.icon = HomeIconRegistry.fromLegacyHomeIcon(legacyIcon);
    }

    public String getIconSaveId() {
        return icon.getSaveId();
    }

    @Override
    public String toString() {
        return "Home{" +
                "owner=" + owner +
                ", name='" + name + '\'' +
                ", location=" + serializeLocation() +
                ", icon=" + icon.getId() +
                '}';
    }
}

package fr.openmc.core.features.corpse.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.bukkit.serializer.BukkitSerializer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "corpses")
public class DBCorpse {
    @DatabaseField(id = true, columnName = "player", canBeNull = false)
        private UUID playerUUID;

        @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
        private byte[] inventoryContent;
        @DatabaseField(canBeNull = false)
        private float exp;
        @DatabaseField(canBeNull = false)
        private int level;

        @DatabaseField(canBeNull = false)
        private boolean killByPlayer;

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

    DBCorpse() {
        // required for ORMLite
    }

    public DBCorpse(UUID playerUUID, ItemStack[] inventoryContent, Location location, float exp, int level, boolean killByPlayer) {
        this.playerUUID = playerUUID;

        try {
            this.inventoryContent = BukkitSerializer.serializeItemStacks(inventoryContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.exp = exp;
        this.level = level;
        this.killByPlayer = killByPlayer;

        setLocation(location);
    }

    public void setLocation(Location location) {
        world = location.getWorld().getName();
        x = (double) Math.round(location.getX() * 10d) / 10d;
        y = (double) Math.round(location.getY() * 10d) / 10d;
        z = (double) Math.round(location.getZ() * 10d) / 10d;
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    public ItemStack[] getInventoryContent() {
        return BukkitSerializer.deserializeItemStacks(inventoryContent);
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}

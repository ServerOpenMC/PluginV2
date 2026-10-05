package fr.openmc.core.features.singularity.sub.wormhole.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

@DatabaseTable(tableName = "worm_loc")
public class WormHoleDB {
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @DatabaseField(columnName = "owner_uuid")
    @Getter
    private UUID ownerUUID;
    @DatabaseField
    private double x;
    @DatabaseField
    private double y;
    @DatabaseField
    private double z;
    @DatabaseField
    private String world;

    WormHoleDB() {
        // required for ORMLite
    }

    public WormHoleDB(UUID ownerUUID, Location wormLocation) {
        this.ownerUUID = ownerUUID;
        this.world = wormLocation.getWorld().getName();
        this.x = wormLocation.getX();
        this.y = wormLocation.getY();
        this.z = wormLocation.getZ();
    }

    public Location getLocation() {
        if (this.world == null || this.world.isBlank())
            return null;

        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }
}
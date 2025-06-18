package fr.openmc.core.features.city.sub.mayor.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@DatabaseTable(tableName = "laws")
public class CityLaw {
    @DatabaseField(id = true)
    @Getter
    private String cityUUID;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private boolean pvp;
    @DatabaseField
    private double warpX;
    @DatabaseField
    private double warpY;
    @DatabaseField
    private double warpZ;
    @DatabaseField
    private float warpYaw;
    @DatabaseField
    private float warpPitch;
    @DatabaseField
    private String warpWorld;

    CityLaw() {
        // required for ORMLite
    }

    public CityLaw(String cityUUID, boolean pvp, Location warp) {
        this.cityUUID = cityUUID;
        this.pvp = pvp;
        setWarp(warp);
    }

    public Location getWarp() {
        return new Location(Bukkit.getWorld(this.warpWorld), this.warpX, this.warpY, this.warpZ, this.warpYaw,
                this.warpPitch);
    }

    public void setWarp(Location warp) {
        if (warp == null)
            return;

        this.warpX = warp.getX();
        this.warpY = warp.getY();
        this.warpZ = warp.getZ();
        this.warpPitch = warp.getPitch();
        this.warpYaw = warp.getYaw();
        this.warpWorld = warp.getWorld().getName();
    }
}

package fr.openmc.core.features.city.models;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;

@DatabaseTable(tableName = "laws")
public class CityLaw {
    @DatabaseField(id = true)
    private String city;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private boolean pvp;
    @DatabaseField(canBeNull = false)
    private double warpX;
    @DatabaseField(canBeNull = false)
    private double warpY;
    @DatabaseField(canBeNull = false)
    private double warpZ;
    @DatabaseField(canBeNull = false)
    private float warpYaw;
    @DatabaseField(canBeNull = false)
    private float warpPitch;
    @DatabaseField(canBeNull = false)
    private String warpWorld;

    CityLaw() {
        // required for ORMLite
    }

    public CityLaw(String city, boolean pvp, Location warp) {
        this.city = city;
        this.pvp = pvp;
        setWarp(warp);
    }

    public City getCity() {
        return CityManager.getCity(city);
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

package fr.openmc.core.features.city.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "laws")
@Setter
@Getter
public class CityLaw {
    @DatabaseField(id = true)
    private String city;
    @DatabaseField(canBeNull = false)
    private boolean pvp;
    @DatabaseField(canBeNull = false)
    private double warpX;
    @DatabaseField(canBeNull = false)
    private double warpY;
    @DatabaseField(canBeNull = false)
    private double warpZ;
    @DatabaseField(canBeNull = false)
    private String warpWorld;

    CityLaw() {
        // required for ORMLite
    }

    public CityLaw(String city, boolean pvp, Location warp) {
        this.city = city;
        this.pvp = pvp;
        this.warpX = warp.getX();
        this.warpY = warp.getY();
        this.warpZ = warp.getZ();
        this.warpWorld = warp.getWorld().getName();
    }

}

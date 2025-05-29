package fr.openmc.core.features.city.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sk89q.worldedit.math.BlockVector2;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;

@DatabaseTable(tableName = "city_regions")
public class DBCityClaim {
    @DatabaseField(canBeNull = false)
    private String city;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int z;

    DBCityClaim() {
        // required for ORMLite
    }

    public DBCityClaim(BlockVector2 chunk, String city) {
        this.city = city;
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    public City getCity() {
        return CityManager.getCity(city);
    }

    public BlockVector2 getBlockVector() {
        return BlockVector2.at(x, z);
    }
}

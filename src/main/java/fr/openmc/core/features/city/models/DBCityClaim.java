package fr.openmc.core.features.city.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.ChunkPos;
import lombok.Getter;

@DatabaseTable(tableName = "city_regions")
public class DBCityClaim {
    @DatabaseField(canBeNull = false)
    @Getter
    private String city;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int z;

    DBCityClaim() {
        // required for ORMLite
    }

    public DBCityClaim(ChunkPos chunk, String city) {
        this.city = city;
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos(x, z);
    }
}

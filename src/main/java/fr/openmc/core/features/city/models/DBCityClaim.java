package fr.openmc.core.features.city.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.ChunkPos;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "city_regions")
public class DBCityClaim {
    @DatabaseField(columnName = "city", canBeNull = false)
    @Getter
    private UUID cityUUID;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int z;

    DBCityClaim() {
        // required for ORMLite
    }

    public DBCityClaim(ChunkPos chunk, UUID cityUUID) {
        this.cityUUID = cityUUID;
        this.x = chunk.x();
        this.z = chunk.z();
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos(x, z);
    }
}

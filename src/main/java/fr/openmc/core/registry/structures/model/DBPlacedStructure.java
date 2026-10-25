package fr.openmc.core.registry.structures.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

/**
 * Stores one placed structure and the blocks that were there before it was placed.
 */
@Getter
@DatabaseTable(tableName = "structures_placed")
public class DBPlacedStructure {

    @DatabaseField(id = true, dataType = DataType.UUID)
    private UUID id;

    @DatabaseField(canBeNull = false)
    private String structureId;

    @DatabaseField(canBeNull = false)
    private String world;

    @DatabaseField
    private int x;

    @DatabaseField
    private int y;

    @DatabaseField
    private int z;

    @DatabaseField(dataType = DataType.BYTE_ARRAY, canBeNull = false)
    private byte[] backup;

    @DatabaseField
    private long createdAt;

    public DBPlacedStructure() {
        // Required by ORMLite.
    }

    public DBPlacedStructure(UUID id, String structureId, String world,
                             int x, int y, int z, byte[] backup) {
        this.id = id;
        this.structureId = structureId;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.backup = backup;
        this.createdAt = System.currentTimeMillis();
    }

}

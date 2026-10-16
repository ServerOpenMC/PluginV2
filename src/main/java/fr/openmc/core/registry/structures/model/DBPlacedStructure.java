package fr.openmc.core.registry.structures.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

/** Une instance de structure posée dans le monde + la sauvegarde de ce qu'il y avait avant (undo). */
@Getter
@DatabaseTable(tableName = "structures_placed")
public class DBPlacedStructure {

    /** Identifiant unique de CE placement (pas de la structure). */
    @DatabaseField(id = true, dataType = DataType.UUID)
    private UUID id;

    /** Id de la CustomStructure (ex: "maison"). Plusieurs placements peuvent avoir le même. */
    @DatabaseField(canBeNull = false, index = true)
    private String structureId;

    @DatabaseField(canBeNull = false)
    private String world; // UUID du monde

    // Coin minimum de la zone sauvegardée
    @DatabaseField private int x;
    @DatabaseField private int y;
    @DatabaseField private int z;

    @DatabaseField(dataType = DataType.BYTE_ARRAY, canBeNull = false)
    private byte[] backup; // schematic WorldEdit de la zone AVANT placement

    @DatabaseField private long createdAt;

    public DBPlacedStructure() {} // requis par ORMLite

    public DBPlacedStructure(UUID id, String structureId, String world, int x, int y, int z, byte[] backup) {
        this.id = id;
        this.structureId = structureId;
        this.world = world;
        this.x = x; this.y = y; this.z = z;
        this.backup = backup;
        this.createdAt = System.currentTimeMillis();
    }

}

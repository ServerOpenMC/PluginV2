package fr.openmc.core.features.events.contents.christmas.mecanism.gift.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasDimensionManager;
import fr.openmc.core.utils.world.block.BlockPos;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.UUID;

@DatabaseTable(tableName = "christmas_gift")
@Getter
public class DBGift {
    @DatabaseField(generatedId = true)
    private Integer id;

    @Setter
    @DatabaseField(canBeNull = false)
    private String type;

    @Setter
    @DatabaseField(columnName = "x")
    private Integer christmasX;
    @Setter
    @DatabaseField(columnName = "y")
    private Integer christmasY;
    @Setter
    @DatabaseField(columnName = "z")
    private Integer christmasZ;

    @DatabaseField(columnName = "entity_uuid")
    private UUID entityUUID;

    DBGift() {
        // Default constructor for ORMLite
    }

    public DBGift(GiftType type, BlockPos blockPos, Entity entity) {
        this.type = type.name();
        this.christmasX = blockPos.x();
        this.christmasY = blockPos.y();
        this.christmasZ = blockPos.z();
        this.entityUUID = entity.getUniqueId();
    }

    public Entity getEntity() {
        return Bukkit.getEntity(entityUUID);
    }

    public GiftType getType() {
        return GiftType.valueOf(type);
    }

    public BlockPos getBlockPos() {
        return new BlockPos(ChristmasDimensionManager.CHRISTMAS_WORLD.getUID(),
                christmasX, christmasY, christmasZ);
    }

    public void removeGiftEntity() {
        Entity entity = getEntity();

        if (entity != null) {
            entity.remove();
        }
    }
}

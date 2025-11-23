package fr.openmc.core.features.dream.mecanism.singularity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@DatabaseTable(tableName = "singularity_contents")
public class SingularityContents {
    @Getter
    @DatabaseField(columnName = "uuid", id = true)
    private UUID playerUUID;
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] content;

    SingularityContents() {
        // required for ORMLite
    }

    public SingularityContents(UUID playerUUID, ItemStack[] content) {
        this.playerUUID = playerUUID;

        this.setContent(content);
    }

    public ItemStack[] getContent() {
        return BukkitSerializer.deserializeItemStacks(content);
    }

    public void setContent(ItemStack[] content) {
        try {
            this.content = BukkitSerializer.serializeItemStacks(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

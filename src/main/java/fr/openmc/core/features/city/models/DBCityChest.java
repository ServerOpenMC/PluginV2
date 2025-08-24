package fr.openmc.core.features.city.models;

import org.bukkit.inventory.ItemStack;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "city_chests")
public class DBCityChest {
    @DatabaseField(columnName = "city", canBeNull = false)
    private UUID cityUUID;
    @DatabaseField(canBeNull = false)
    @Getter
    private int page;
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] content;

    DBCityChest() {
        // required for ORMLite
    }

    public DBCityChest(UUID cityUUID, int page, ItemStack[] content) {
        this.cityUUID = cityUUID;
        this.page = page;

        try {
            this.content = BukkitSerializer.serializeItemStacks(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ItemStack[] getContent() {
        try {
            return BukkitSerializer.deserializeItemStacks(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

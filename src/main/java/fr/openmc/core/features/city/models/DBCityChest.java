package fr.openmc.core.features.city.models;

import org.bukkit.inventory.ItemStack;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;

@DatabaseTable(tableName = "city_chests")
public class DBCityChest {
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String city;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    @Getter
    private int page;
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] content;

    DBCityChest() {
        // required for ORMLite
    }

    public DBCityChest(String city, int page, ItemStack[] content) {
        this.city = city;
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

package fr.openmc.core.features.city.sub.mayor.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

@DatabaseTable(tableName = "mayor_constants")
public class MayorConstant {
    @DatabaseField(id = true)
    private final int id = 1;
    @DatabaseField(canBeNull = false)
    @Getter
    private int phase;

    MayorConstant() {
        // required for ORMLite
    }

    public MayorConstant(int phase) {
        this.phase = phase;
    }
}

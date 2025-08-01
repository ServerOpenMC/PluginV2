package fr.openmc.core.features.city.sub.notation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.DateUtils;
import lombok.Getter;

@DatabaseTable(tableName = "city_notation")
@Getter
public class CityNotation {
    @DatabaseField(id = true, columnName = "uuid")
    private String cityUUID;
    @DatabaseField
    private String weekStr;
    @DatabaseField(defaultValue = "0", columnName = "architectural")
    private double noteArchitectural;
    @DatabaseField(defaultValue = "0", columnName = "coherence")
    private double noteCoherence;
    @DatabaseField
    private String description;

    CityNotation() {
        // required for ORMLite
    }

    public CityNotation(String uuid, double noteArchitectural, double noteCoherence, String description) {
        this.cityUUID = uuid;
        this.noteArchitectural = noteArchitectural;
        this.noteCoherence = noteCoherence;
        this.weekStr = DateUtils.getWeekFormat();
        this.description = description;
    }
}

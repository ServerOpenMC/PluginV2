package fr.openmc.core.features.city.sub.notation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@DatabaseTable(tableName = "city_notation")
@Getter
@Setter
public class CityNotation {
    @DatabaseField(id = true, columnName = "city_uuid")
    private UUID cityUUID;
    @DatabaseField
    private String weekStr;
    @DatabaseField(defaultValue = "0", columnName = "economy")
    private Double noteEconomy;
    @DatabaseField(defaultValue = "0", columnName = "activity")
    private Double noteActivity;
    @DatabaseField(defaultValue = "0", columnName = "architectural")
    private double noteArchitectural;
    @DatabaseField(defaultValue = "0", columnName = "coherence")
    private double noteCoherence;
    @DatabaseField(defaultValue = "0", columnName = "money")
    private double money;
    @DatabaseField
    private String description;

    CityNotation() {
        // required for ORMLite
    }

    public CityNotation(UUID uuid, double noteArchitectural, double noteCoherence, String description, String weekStr) {
        this.cityUUID = uuid;
        this.noteArchitectural = noteArchitectural;
        this.noteCoherence = noteCoherence;
        this.weekStr = weekStr;
        this.description = description;
    }

    public double getTotalNote() {
        double total = 0;
        if (noteEconomy != null) {
            total += noteEconomy;
        }
        if (noteActivity != null) {
            total += noteActivity;
        }
        total += noteArchitectural + noteCoherence;
        return total;
    }
}

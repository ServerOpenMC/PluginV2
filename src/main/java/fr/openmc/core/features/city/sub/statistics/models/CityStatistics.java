package fr.openmc.core.features.city.sub.statistics.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@DatabaseTable(tableName = "city_statistics")
public class CityStatistics {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, columnName = "city_uuid")
    private UUID cityUUID;

    @DatabaseField(canBeNull = false)
    @Setter
    private String scope;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    @Setter
    private Serializable value;

    CityStatistics() {
    }

    public CityStatistics(UUID cityUUID, String scope, Serializable value) {
        this.cityUUID = cityUUID;
        this.scope = scope;
        this.value = value;
    }

    public int asInt() {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    public long asLong() {
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    public double asDouble() {
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    public String asString() {
        return value != null ? value.toString() : null;
    }
}
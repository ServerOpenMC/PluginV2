package fr.openmc.core.features.settings.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.settings.SettingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@DatabaseTable(tableName = "player_settings")
@Getter
@Setter
@NoArgsConstructor
public class PlayerSettingEntity {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "playerUUID", canBeNull = false)
    private String playerUUID;

    @DatabaseField(columnName = "settingType", canBeNull = false)
    private String settingType;

    @DatabaseField(columnName = "settingValue", canBeNull = false)
    private String settingValue;

    public PlayerSettingEntity(UUID playerUUID, SettingType settingType, Object value) {
        this.playerUUID = playerUUID.toString();
        this.settingType = settingType.name();
        this.settingValue = value.toString();
    }

    public SettingType getSettingTypeAsEnum() {
        return SettingType.valueOf(settingType);
    }

    public void updateValue(Object value) {
        this.settingValue = value.toString();
    }
}
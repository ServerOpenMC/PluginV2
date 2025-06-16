package fr.openmc.core.features.city.models;

import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.utils.ColorUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mayors")
public class Mayor {
    @DatabaseField(id = true)
    @Getter
    private String city;
    @DatabaseField
    @Getter
    @Setter
    private UUID UUID;
    @DatabaseField
    @Getter
    @Setter
    private String name;
    @DatabaseField
    private String mayorColor;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private int idPerk1;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private int idPerk2;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private int idPerk3;
    @DatabaseField(canBeNull = false, columnName = "election_type")
    private String electionType;

    Mayor() {
        // required for ORMLite
    }

    public Mayor(String city, String mayorName, UUID mayorUUID, NamedTextColor mayorColor, int idPerk1, int idPerk2,
            int idPerk3, ElectionType electionType) {
        this.city = city;
        this.name = mayorName;
        this.UUID = mayorUUID;
        setMayorColor(mayorColor);
        this.idPerk1 = idPerk1;
        this.idPerk2 = idPerk2;
        this.idPerk3 = idPerk3;
        setElectionType(electionType);
    }

    public NamedTextColor getMayorColor() {
        return ColorUtils.getNamedTextColor(mayorColor);
    }

    public void setMayorColor(NamedTextColor color) {
        this.mayorColor = color == null ? null : color.toString();
    }

    public ElectionType getElectionType() {
        return ElectionType.valueOf(this.electionType);
    }

    public void setElectionType(ElectionType type) {
        this.electionType = type.name();
    }
}

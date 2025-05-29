package fr.openmc.core.features.city.models;

import fr.openmc.core.features.city.mayor.ElectionType;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mayors")
public class Mayor {
    @DatabaseField(id = true)
    private String city;
    @DatabaseField(canBeNull = false)
    private UUID id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private String mayorColor;
    @DatabaseField(canBeNull = false)
    private int idPerk1;
    @DatabaseField(canBeNull = false)
    private int idPerk2;
    @DatabaseField(canBeNull = false)
    private int idPerk3;
    @DatabaseField(canBeNull = false)
    private String electionType;

    Mayor() {
        // required for ORMLite
    }

    public Mayor(String city, String mayorName, UUID mayorUUID, NamedTextColor mayorColor, int idPerk1, int idPerk2,
            int idPerk3, ElectionType electionType) {
        this.city = city;
        this.name = mayorName;
        this.id = mayorUUID;
        this.mayorColor = mayorColor.toString();
        this.idPerk1 = idPerk1;
        this.idPerk2 = idPerk2;
        this.idPerk3 = idPerk3;
        this.electionType = electionType.name();
    }
}

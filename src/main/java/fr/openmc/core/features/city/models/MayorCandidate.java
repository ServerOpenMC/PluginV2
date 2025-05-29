package fr.openmc.core.features.city.models;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@Getter
@DatabaseTable(tableName = "candidates")
public class MayorCandidate {
    @DatabaseField(id = true)
    private UUID id;
    @DatabaseField(canBeNull = false)
    private String city;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    @Setter
    private String candidateColor;
    @DatabaseField(canBeNull = false)
    private int idChoicePerk2;
    @DatabaseField(canBeNull = false)
    private int idChoicePerk3;
    @DatabaseField(canBeNull = false)
    @Setter
    private int vote;

    MayorCandidate() {
        // required for ORMLite
    }

    public MayorCandidate(String city, String candidateName, UUID candidateUUID, NamedTextColor candidateColor,
            int idChoicePerk2, int idChoicePerk3, int vote) {
        this.city = city;
        this.name = candidateName;
        this.id = candidateUUID;
        this.candidateColor = candidateColor.toString();
        this.idChoicePerk2 = idChoicePerk2;
        this.idChoicePerk3 = idChoicePerk3;
        this.vote = vote;
    }
}

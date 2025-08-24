package fr.openmc.core.features.city.sub.mayor.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.ColorUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

@DatabaseTable(tableName = "mayor_candidates")
public class MayorCandidate {
    @DatabaseField(id = true)
    @Getter
    private UUID candidateUUID;
    @DatabaseField(canBeNull = false)
    @Getter
    private UUID cityUUID;
    @DatabaseField(canBeNull = false)
    @Getter
    private String name;
    @DatabaseField
    private String candidateColor;
    @DatabaseField(canBeNull = false)
    @Getter
    private int idChoicePerk2;
    @DatabaseField(canBeNull = false)
    @Getter
    private int idChoicePerk3;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private int vote;

    MayorCandidate() {
        // required for ORMLite
    }

    public MayorCandidate(UUID cityUUID, String candidateName, UUID candidateUUID, NamedTextColor candidateColor,
            int idChoicePerk2, int idChoicePerk3, int vote) {
        this.cityUUID = cityUUID;
        this.name = candidateName;
        this.candidateUUID = candidateUUID;
        this.candidateColor = candidateColor.toString();
        this.idChoicePerk2 = idChoicePerk2;
        this.idChoicePerk3 = idChoicePerk3;
        this.vote = vote;
    }

    public NamedTextColor getCandidateColor() {
        return ColorUtils.getNamedTextColor(candidateColor);
    }

    public void setCandidateColor(NamedTextColor color) {
        this.candidateColor = color == null ? null : color.toString();
    }
}

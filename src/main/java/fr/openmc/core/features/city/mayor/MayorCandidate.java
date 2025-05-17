package fr.openmc.core.features.city.mayor;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

@Getter
public class MayorCandidate {
    private final City city;
    private final String name;
    private final UUID UUID;
    @Setter private NamedTextColor candidateColor;
    private final int idChoicePerk2;
    private final int idChoicePerk3;
    @Setter private int vote;

    public MayorCandidate(City city, String candidateName, UUID candidateUUID, NamedTextColor candidateColor, int idChoicePerk2, int idChoicePerk3, int vote) {
        this.city = city;
        this.name = candidateName;
        this.UUID = candidateUUID;
        this.candidateColor = candidateColor;
        this.idChoicePerk2 = idChoicePerk2;
        this.idChoicePerk3 = idChoicePerk3;
        this.vote = vote;
    }
}

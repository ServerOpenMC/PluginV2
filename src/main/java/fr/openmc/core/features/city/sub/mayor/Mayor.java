package fr.openmc.core.features.city.sub.mayor;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

@Getter
@Setter
public class Mayor {
    private City city;
    private String name;
    private UUID UUID;
    private NamedTextColor mayorColor;
    private int idPerk1;
    private int idPerk2;
    private int idPerk3;
    private ElectionType electionType;

    public Mayor(City city, String mayorName, UUID mayorUUID, NamedTextColor mayorColor, int idPerk1, int idPerk2, int idPerk3, ElectionType electionType) {
        this.city = city;
        this.name = mayorName;
        this.UUID = mayorUUID;
        this.mayorColor = mayorColor;
        this.idPerk1 = idPerk1;
        this.idPerk2 = idPerk2;
        this.idPerk3 = idPerk3;
        this.electionType = electionType;
    }
}

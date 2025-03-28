package fr.openmc.core.features.city.mayor;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public class Mayor {
    private final City city;
    private final String mayorName;
    private final String mayorUUID;
    private final NamedTextColor mayorColor;
    private final int idPerk1;
    private final int idPerk2;
    private final int idPerk3;

    public Mayor(City city, String mayorName, String mayorUUID, NamedTextColor mayorColor, int idPerk1, int idPerk2, int idPerk3) {
        this.city = city;
        this.mayorName = mayorName;
        this.mayorUUID = mayorUUID;
        this.mayorColor = mayorColor;
        this.idPerk1 = idPerk1;
        this.idPerk2 = idPerk2;
        this.idPerk3 = idPerk3;
    }
}

package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;

public interface CityMayor {
    int getMayorPhase();

    Mayor getMayor();

    boolean hasMayor();

    ElectionType getElectionType();

    CityLaw getLaw();
}

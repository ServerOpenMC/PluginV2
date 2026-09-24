package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;

public interface CityMayor {
    MayorPhase getMayorPhase();

    Mayor getMayor();

    boolean hasMayor();

    ElectionType getElectionType();

    CityLaw getLaw();
}

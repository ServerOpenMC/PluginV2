package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.models.WarHistory;

public interface CityWar {
    boolean isInWar();

    War getWar();

    WarHistory getWarHistory();

    int getPowerPoints();

    void updatePowerPoints(int diff);
}

package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.core.features.city.sub.statistics.models.CityStatistics;

import java.io.Serializable;

public interface CityStatistic {
    CityStatistics getOrCreateStat(String scope);

    void setStat(String scope, Serializable value);

    void removeStats();

    void incrementStats(String scope, long amount);

    Object getStatValue(String scope);
}

package fr.openmc.core.features.city.sub.mayor.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "mayor_votes")
public class MayorVote {
    private final MayorManager mayorManager = OMCRegistry.CITY_FEATURES.MAYOR;

    @DatabaseField(id = true)
    @Getter
    private UUID voterUUID;
    @DatabaseField(columnName = "city_uuid", canBeNull = false)
    @Getter
    private UUID cityUUID;
    @DatabaseField(canBeNull = false)
    private UUID candidateUUID;

    MayorVote() {
        // required for ORMLite
    }

    public MayorVote(UUID cityUUID, UUID voterUUID, MayorCandidate candidate) {
        this.cityUUID = cityUUID;
        this.voterUUID = voterUUID;
        this.candidateUUID = candidate.getCandidateUUID();
    }

    public City getCity() {
        return City.of(cityUUID);
    }

    public MayorCandidate getCandidateUUID() {
        return mayorManager.getCandidate(candidateUUID);
    }
}

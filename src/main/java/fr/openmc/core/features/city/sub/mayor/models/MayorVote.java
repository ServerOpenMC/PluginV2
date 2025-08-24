package fr.openmc.core.features.city.sub.mayor.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "mayor_votes")
public class MayorVote {
    @DatabaseField(id = true)
    @Getter
    private UUID voter;
    @DatabaseField(canBeNull = false)
    private UUID cityUUID;
    @DatabaseField(canBeNull = false)
    private UUID candidate;

    MayorVote() {
        // required for ORMLite
    }

    public MayorVote(UUID cityUUID, UUID voterUUID, MayorCandidate candidate) {
        this.cityUUID = cityUUID;
        this.voter = voterUUID;
        this.candidate = candidate.getCandidateUUID();
    }

    public City getCity() {
        return CityManager.getCity(cityUUID);
    }

    public MayorCandidate getCandidate() {
        return MayorManager.getCandidate(voter);
    }
}

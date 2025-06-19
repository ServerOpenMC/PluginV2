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
    private String city;
    @DatabaseField(canBeNull = false)
    private UUID candidate;

    MayorVote() {
        // required for ORMLite
    }

    public MayorVote(String city, UUID voterUUID, MayorCandidate candidate) {
        this.city = city;
        this.voter = voterUUID;
        this.candidate = candidate.getUUID();
    }

    public City getCity() {
        return CityManager.getCity(city);
    }

    public MayorCandidate getCandidate() {
        return MayorManager.getCandidate(voter);
    }
}

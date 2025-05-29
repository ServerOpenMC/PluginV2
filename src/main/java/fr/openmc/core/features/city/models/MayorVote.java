package fr.openmc.core.features.city.models;

import lombok.Getter;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "votes")
@Getter
public class MayorVote {
    @DatabaseField(id = true)
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
        this.candidate = candidate.getId();
    }
}

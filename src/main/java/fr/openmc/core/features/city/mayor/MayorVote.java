package fr.openmc.core.features.city.mayor;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MayorVote {
    private final UUID voterUUID;
    private final MayorCandidate candidate;

    public MayorVote(UUID voterUUID, MayorCandidate candidate) {
        this.voterUUID = voterUUID;
        this.candidate = candidate;
    }
}

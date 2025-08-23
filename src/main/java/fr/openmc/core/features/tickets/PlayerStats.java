package fr.openmc.core.features.tickets;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class PlayerStats {

    public UUID uniqueID;
    public int timePlayed;
    @Setter
    public boolean ticketGiven;

    PlayerStats(UUID uuid, int timePlayed, boolean hasTicketGiven) {
        this.uniqueID = uuid;
        this.timePlayed = timePlayed;
    }

    PlayerStats(UUID uuid, int timePlayed) {
        this(uuid, timePlayed, false);
    }

}

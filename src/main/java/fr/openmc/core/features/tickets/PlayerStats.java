package fr.openmc.core.features.tickets;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class PlayerStats {

    public UUID uniqueID;
    public int timePlayed;
    @Setter public int ticketRemaining;
    @Setter public boolean ticketGiven;
    @Setter public Map<String, Integer> maxItemsGiven;

    PlayerStats(UUID uuid, int timePlayed, int ticketRemaining, boolean hasTicketGiven, Map<String, Integer> maxItemsGiven) {
        this.uniqueID = uuid;
        this.timePlayed = timePlayed;
        this.ticketRemaining = ticketRemaining;
        this.ticketGiven = hasTicketGiven;
        this.maxItemsGiven = maxItemsGiven;
    }

}

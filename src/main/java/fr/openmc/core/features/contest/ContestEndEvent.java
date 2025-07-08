package fr.openmc.core.features.contest;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import fr.openmc.core.features.contest.models.Contest;

import java.util.List;
import java.util.UUID;

public class ContestEndEvent extends Event {

    @Getter
    private final Contest contestData;
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final List<UUID> winners;
    @Getter
    private final List<UUID> losers;

    /**
     * @param contestData The contest data
     * @param winners     The list of winners
     * @param losers      The list of losers
     */
    public ContestEndEvent(Contest contestData, List<UUID> winners, List<UUID> losers) {
        this.contestData = contestData;
        this.winners = winners;
        this.losers = losers;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}

package fr.openmc.core.features.contest;

import fr.openmc.core.features.contest.models.Contest;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Événement déclenché à la fin d'un contest.
 * Contient les données du contest ainsi que la liste des gagnants et des perdants.
 */
@Getter
public class ContestEndEvent extends Event {

    /**
     * Les données associées au contest.
     */
    private final Contest contestData;

    /**
     * Liste statique des handlers pour l'événement.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Liste des UUID des gagnants du contest.
     */
    private final List<UUID> winners;

    /**
     * Liste des UUID des perdants du contest.
     */
    private final List<UUID> losers;

    /**
     * Constructeur de ContestEndEvent.
     *
     * @param contestData Les données du contest
     * @param winners     La liste des gagnants
     * @param losers      La liste des perdants
     */
    public ContestEndEvent(Contest contestData, List<UUID> winners, List<UUID> losers) {
        this.contestData = contestData;
        this.winners = winners;
        this.losers = losers;
    }

    /**
     * Récupère la liste des handlers de l'événement.
     *
     * @return la liste des handlers
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Retourne la liste des handlers de cet événement.
     *
     * @return la liste des handlers
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}

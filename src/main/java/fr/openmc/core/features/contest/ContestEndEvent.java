package fr.openmc.core.features.contest;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContestEndEvent extends Event {
	
	@Getter
	private final ContestData contestData;
	private static final HandlerList HANDLERS = new HandlerList();
	@Getter
	private final List<OfflinePlayer> winners;
	@Getter
	private final List<OfflinePlayer> losers;
	
	/**
	 * @param contestData The contest data
	 * @param winners The list of winners
	 * @param losers The list of losers
	 */
	public ContestEndEvent(ContestData contestData, List<OfflinePlayer> winners, List<OfflinePlayer> losers) {
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

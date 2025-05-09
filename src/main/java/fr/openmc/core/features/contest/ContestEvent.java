package fr.openmc.core.features.contest;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContestEvent extends Event {
	
	private static final HandlerList HANDLERS = new HandlerList();
	@Getter
	private final ContestData contestData;
	@Getter
	private final List<Player> winners;
	@Getter
	private final List<Player> losers;
	
	public ContestEvent(ContestData contestData, List<Player> winners, List<Player> losers) {
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

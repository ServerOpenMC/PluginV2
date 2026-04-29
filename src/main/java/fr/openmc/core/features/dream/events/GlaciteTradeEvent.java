package fr.openmc.core.features.dream.events;

import fr.openmc.core.features.dream.mecanism.tradernpc.GlaciteTrade;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class GlaciteTradeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final GlaciteTrade trade;

    /**
     * @param player The player whose dream time has ended
     */
    public GlaciteTradeEvent(Player player, GlaciteTrade trade) {
        this.player = player;
        this.trade = trade;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}

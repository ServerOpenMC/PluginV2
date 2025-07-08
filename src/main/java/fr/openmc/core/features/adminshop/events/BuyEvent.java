package fr.openmc.core.features.adminshop.events;

import fr.openmc.core.features.adminshop.ShopItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class BuyEvent extends Event {

    private final ShopItem item;
    private final Player player;

    private static final HandlerList HANDLERS = new HandlerList();

    public BuyEvent(Player player, ShopItem item) {
        this.player = player;
        this.item = item;

    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}

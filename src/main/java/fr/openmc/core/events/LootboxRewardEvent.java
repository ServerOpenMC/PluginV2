package fr.openmc.core.events;

import fr.openmc.core.registry.lootboxes.CustomLootbox;
import fr.openmc.core.registry.loottable.CustomLoot;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class LootboxRewardEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final CustomLoot loot;
    private final CustomLootbox box;
    private boolean cancelled = false;

    public LootboxRewardEvent(Player player, CustomLootbox box, CustomLoot loot) {
        super(player);
        this.loot = loot;
        this.box = box;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}

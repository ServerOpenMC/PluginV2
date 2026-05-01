package fr.openmc.core.registry.items.contents;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class UseHammerEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    public Hammer hammer;
    @Getter
    public Player player;
    @Getter
    public int countBlockBroken;

    protected UseHammerEvent(@NotNull Block block, Hammer hammer, Player player, int countBlockBroken) {
        super(block);
        this.hammer = hammer;
        this.player = player;
        this.countBlockBroken = countBlockBroken;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}

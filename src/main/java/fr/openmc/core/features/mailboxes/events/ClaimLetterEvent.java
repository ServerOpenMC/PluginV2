package fr.openmc.core.features.mailboxes.events;

import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ClaimLetterEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Letter letter;

    public ClaimLetterEvent(Player player, Letter letter) {
        super(player);
        this.letter = letter;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}

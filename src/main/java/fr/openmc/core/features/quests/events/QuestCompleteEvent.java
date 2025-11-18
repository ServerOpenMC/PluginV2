package fr.openmc.core.features.quests.events;

import fr.openmc.core.features.quests.objects.Quest;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class QuestCompleteEvent extends Event {

    private final Player player;
    private final Quest quest;

    private static final HandlerList HANDLERS = new HandlerList();

    public QuestCompleteEvent(Player player, Quest quest) {
        this.player = player;
        this.quest = quest;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}

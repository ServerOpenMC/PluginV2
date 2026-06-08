package fr.openmc.core.features.events.contents.dailyevents.models;


import fr.openmc.core.features.events.models.Event;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Wrapper qui combine le DailyEvent et sa date d'execution
 */
@Getter
public class ScheduleDailyEvent extends Event {
    private final DailyEvent dailyEvent;
    private final LocalDateTime scheduledStartDate;
    private final LocalDateTime scheduledEndDate;

    public ScheduleDailyEvent(DailyEvent dailyEvent, LocalDateTime scheduledDate) {
        this.dailyEvent = dailyEvent;
        this.scheduledStartDate = scheduledDate;
        this.scheduledEndDate = scheduledDate.plusMinutes(dailyEvent.getDuration());
    }

    @Override
    public Component getName() {
        return dailyEvent.getName();
    }

    @Override
    public List<Component> getDescription() {
        return dailyEvent.getDescription();
    }

    @Override
    public ItemStack getIcon() {
        return dailyEvent.getIcon();
    }
}

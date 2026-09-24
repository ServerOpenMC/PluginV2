package fr.openmc.core.features.events.contents.dailyevents.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.commands.autocomplete.DailyEventAutoComplete;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"events", "dailyevents"})
@Description("Ouvre l'interface des événements")
public class DailyEventCommand {
    private final DailyEventsManager dailyEventsManager = OMCRegistry.FEATURES.DAILY_EVENTS.get();
    @Subcommand("forceStart")
    @CommandPermission("omc.admins.commands.dailyevent.forcestart")
    public void forceStartCommand(Player player,
                                         @SuggestWith(DailyEventAutoComplete.class) String dailyEvent) {
        if (dailyEventsManager.getOutgoingEvent() != null) {
            // * On arrete l'evenement en cours
            dailyEventsManager.getEndEventTask().cancel();
            dailyEventsManager.setEndEventTask(null);
            dailyEventsManager.getOutgoingEvent().getDailyEvent().end();
        }

        // * on lance le evenement rentré en param
        dailyEventsManager.setOutgoingEvent(new ScheduleDailyEvent(
                OMCRegistry.DAILY_EVENTS.getOrThrow(dailyEvent), DateUtils.getLocalDateTime()));
        dailyEventsManager.getOutgoingEvent().getDailyEvent().start();
    }

    @Subcommand("forceEnd")
    @CommandPermission("omc.admins.commands.dailyevent.forceend")
    public void forceEndCommand(Player player) {
        if (dailyEventsManager.getOutgoingEvent() != null) {
            // * On arrete l'evenement en cours
            dailyEventsManager.getEndEventTask().cancel();
            dailyEventsManager.setEndEventTask(null);
            dailyEventsManager.getOutgoingEvent().getDailyEvent().end();
        }
    }
}

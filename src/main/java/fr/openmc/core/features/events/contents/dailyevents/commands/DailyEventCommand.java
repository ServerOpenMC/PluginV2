package fr.openmc.core.features.events.contents.dailyevents.commands;

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
    @Subcommand("forceStart")
    @CommandPermission("omc.admins.commands.dailyevent.forcestart")
    public static void forceStartCommand(Player player,
                                         @SuggestWith(DailyEventAutoComplete.class) String dailyEvent) {
        if (DailyEventsManager.outgoingEvent != null) {
            // * On arrete l'evenement en cours
            DailyEventsManager.endEventTask.cancel();
            DailyEventsManager.endEventTask = null;
            DailyEventsManager.outgoingEvent.getDailyEvent().end();
        }

        // * on lance le evenement rentré en param
        DailyEventsManager.outgoingEvent = new ScheduleDailyEvent(
                DailyEventsManager.getDailyEvent(dailyEvent), DateUtils.getLocalDateTime());
        DailyEventsManager.outgoingEvent.getDailyEvent().start();
    }

    @Subcommand("forceEnd")
    @CommandPermission("omc.admins.commands.dailyevent.forceend")
    public static void forceEndCommand(Player player) {
        if (DailyEventsManager.outgoingEvent != null) {
            // * On arrete l'evenement en cours
            DailyEventsManager.endEventTask.cancel();
            DailyEventsManager.endEventTask = null;
            DailyEventsManager.outgoingEvent.getDailyEvent().end();
        }
    }
}

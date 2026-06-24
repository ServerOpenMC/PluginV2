package fr.openmc.core.features.events;

import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.features.events.commands.CalendarCommand;
import fr.openmc.core.features.events.commands.EventCommand;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.models.Event;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventsManager extends Feature implements LoadAfterItemsAdder, HasCommands {
    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new CalendarCommand(),
                new EventCommand()
        );
    }

    public static List<Event> getUpcomingEvents(int slots) {
        List<Event> events = new ArrayList<>();

        events.addAll(DailyEventsManager.incomingEvents);
        events.add(WeeklyEventsManager.getCurrentEvent());

        events.sort((e1, e2) -> {
            LocalDateTime d1 = getEventStartDate(e1);
            LocalDateTime d2 = getEventStartDate(e2);
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d1.compareTo(d2);
        });

        if (events.getLast() instanceof WeeklyEvent we) {
            for (int i = events.size(); i <= slots; i++) {
                int weekOffset = i;

                events.add(new WeeklyEvent() {

                    @Override
                    public int getWeekOffset() {
                        return weekOffset;
                    }

                    @Override
                    public List<WeeklyEventPhase> getPhases() {
                        return List.of(new WeeklyEventPhase() {
                            @Override
                            public Component getName() {
                                return TranslationManager.translation("feature.events.calendar.unknown_phase");
                            }

                            @Override
                            public List<Component> getDescription() {
                                return List.of();
                            }

                            @Override
                            public DayOfWeek getStartDay() {
                                return we.getPhases().getFirst().getStartDay();
                            }

                            @Override
                            public int getStartHour() {
                                return 0;
                            }

                            @Override
                            public int getStartMinutes() {
                                return 0;
                            }

                            @Override
                            public Runnable runAction() {
                                return null;
                            }
                        });
                    }

                    @Override
                    public Component getName() {
                        return TranslationManager.translation("feature.events.calendar.weekend_event_name");
                    }

                    @Override
                    public List<Component> getDescription() {
                        return List.of();
                    }

                    @Override
                    public ItemStack getIcon() {
                        return ItemStack.of(Material.GOLD_BLOCK);
                    }
                });
            }
        }

        return events;
    }

    private static LocalDateTime getEventStartDate(Event event) {
        if (event instanceof ScheduleDailyEvent sde) {
            return sde.getScheduledStartDate();
        } else if (event instanceof WeeklyEvent we) {
            WeeklyEventPhase firstPhase = we.getPhases().getFirst();
            LocalDateTime now = DateUtils.getLocalDateTime();
            return now.toLocalDate()
                    .with(TemporalAdjusters.nextOrSame(firstPhase.getStartDay()))
                    .plusWeeks(we.getWeekOffset())
                    .atTime(firstPhase.getStartHour(), firstPhase.getStartMinutes());
        }
        return null;
    }

    public static List<Event> getAllEventsRegistred() {
        List<Event> events = new ArrayList<>(DailyEventsManager.EVENTS);
        events.addAll(WeeklyEventsManager.EVENTS);
        return events;
    }
}

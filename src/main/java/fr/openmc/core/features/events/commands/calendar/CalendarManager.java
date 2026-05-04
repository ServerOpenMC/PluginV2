package fr.openmc.core.features.events.commands.calendar;

import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.models.Event;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CalendarManager extends Feature implements LoadAfterItemsAdder,HasCommands {
    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new CalendarCommand()
        );
    }

    public static List<Event> getUpcomingEvents(int slots) {
        List<Event> events = new ArrayList<>(List.of(
                WeeklyEventsManager.getCurrentEvent()
        ));

        //todo: implement DailyEvent in upcoming events

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
                                return Component.text("Inconnu");
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
                        return Component.text("Evenement du Weekend", NamedTextColor.YELLOW, TextDecoration.BOLD);
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
}

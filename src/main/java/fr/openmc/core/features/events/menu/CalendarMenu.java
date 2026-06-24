package fr.openmc.core.features.events.menu;

import fr.openmc.api.menulib.MenuLib;
import fr.openmc.api.menulib.OpenMenu;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.EventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.models.Event;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarMenu extends PaginatedMenu implements OpenMenu {
    private static final HashMap<String, ScheduleDailyEvent> scheduledEventById = new HashMap<>();

    public CalendarMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Event event : EventsManager.getUpcomingEvents(14)) {
            ItemMenuBuilder itemBuilder = new ItemMenuBuilder(this, event.getIcon(), meta -> {
                meta.customName(event.getName().decoration(TextDecoration.ITALIC, false));
                meta.lore(getEventLore(event));
            });

            if (event instanceof ScheduleDailyEvent de) {
                String itemId = "scheduled_" + de.getScheduledStartDate().toString().toLowerCase(); // .toLowerCase car mc enelve les maj sur les pdc
                scheduledEventById.put(itemId, de);
                items.add(itemBuilder.setItemId(itemId));
            } else if (event instanceof WeeklyEvent) {
                items.add(itemBuilder);
            }
        }
        return items;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        int i = 0;
        for (ItemStack itemStack : event.getInventory().getContents()) {
            if (itemStack == null) {
                i++;
                continue;
            }

            PersistentDataContainerView pdc = itemStack.getPersistentDataContainer();
            if (!pdc.has(MenuLib.getItemIdKey())) {
                i++;
                continue;
            }

            String itemId = pdc.get(MenuLib.getItemIdKey(), PersistentDataType.STRING);
            ScheduleDailyEvent scheduleEvent = scheduledEventById.get(itemId);

            if (scheduleEvent != null) {
                MenuUtils.runDynamicItem(getOwner(), this, i, () ->
                        new ItemMenuBuilder(this, scheduleEvent.getIcon(), meta -> {
                            meta.customName(scheduleEvent.getName().decoration(TextDecoration.ITALIC, false));
                            meta.lore(getEventLore(scheduleEvent));
                        })
                ).runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            }
            i++;
        }
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public int getSizeOfItems() {
        return EventsManager.getUpcomingEvents(14).size();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(33, ItemMenuTemplate.BTN_CANCEL.apply(this));
        return map;
    }

    private List<Component> getEventLore(Event event) {
        List<Component> eventLore = new ArrayList<>(event.getDescription());

        if (event instanceof ScheduleDailyEvent de) {
            LocalDateTime now = DateUtils.getLocalDateTime();
            LocalDateTime startDate = de.getScheduledStartDate();
            LocalDateTime endDate = de.getScheduledStartDate();

            eventLore.add(Component.empty());
            eventLore.add(TranslationManager.translation("feature.events.calendar.start_in",
                    Component.text(DateUtils.convertSecondToTime(ChronoUnit.SECONDS.between(now, startDate)), NamedTextColor.YELLOW)
            ));
            eventLore.add(Component.empty());
            eventLore.add(TranslationManager.translation("feature.events.calendar.start_date",
                    Component.text(DateUtils.formatDate(startDate), NamedTextColor.AQUA),
                    Component.text(DateUtils.formatHourMinute(endDate.getHour(), endDate.getMinute()), NamedTextColor.AQUA)
            ));
            eventLore.add(TranslationManager.translation("feature.events.calendar.end_date",
                    Component.text(DateUtils.formatDate(endDate), NamedTextColor.AQUA),
                    Component.text(DateUtils.formatHourMinute(endDate.getHour(), endDate.getMinute()), NamedTextColor.AQUA)
            ));

        } else if (event instanceof WeeklyEvent we) {
            eventLore.add(Component.empty());
            eventLore.add(TranslationManager.translation("feature.events.calendar.phases"));
            for (WeeklyEventPhase phase : we.getPhases()) {
                LocalDateTime now = DateUtils.getLocalDateTime();

                LocalDate nextDate = now.toLocalDate()
                        .with(TemporalAdjusters.nextOrSame(phase.getStartDay()))
                        .plusWeeks(we.getWeekOffset());

                LocalDateTime dateEvent = nextDate.atTime(
                        phase.getStartHour(),
                        phase.getStartMinutes()
                );

                String formattedDate = DateUtils.formatDate(dateEvent);
                String formattedTime = DateUtils.formatHourMinute(phase.getStartHour(), phase.getStartMinutes());

                eventLore.add(TranslationManager.translation(
                        "feature.events.calendar.phase.line",
                        phase.getName().color(NamedTextColor.GRAY),
                        Component.text(formattedDate).color(NamedTextColor.GRAY),
                        Component.text(formattedTime).color(NamedTextColor.GRAY)
                ));
            }
        }
        return eventLore;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.calendar.title");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}

package fr.openmc.core.features.events.menus;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.managers.CalendarManager;
import fr.openmc.core.features.events.models.Event;
import fr.openmc.core.registry.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class CalendarMenu extends PaginatedMenu {
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
        for (Event event : CalendarManager.getUpcomingEvents(14)) {
            items.add(new ItemBuilder(this, event.getIcon(), meta -> {
                meta.customName(event.getName().decoration(TextDecoration.ITALIC, false));
                meta.lore(getEventLore(event));
            }));
        }
        return items;
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
        return getItems().size();
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(33, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_cancel")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§7Fermer"))).setCloseButton());
        return map;
    }

    private List<Component> getEventLore(Event event) {
        List<Component> eventLore = new ArrayList<>(event.getDescription());

        if (event instanceof WeeklyEvent we) {
            eventLore.add(Component.empty());
            eventLore.add(Component.text("Phases :", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            for (WeeklyEventPhase phase : we.getPhases()) {
                LocalDateTime now = LocalDateTime.now();

                LocalDate nextDate = now.toLocalDate()
                        .with(TemporalAdjusters.nextOrSame(phase.getStartDay()))
                        .plusWeeks(we.getWeekOffset());

                LocalDateTime dateEvent = nextDate.atTime(
                        phase.getStartHour(),
                        phase.getStartMinutes()
                );

                String formattedDate = dateEvent.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRANCE)
                        + " " + dateEvent.getDayOfMonth() + " "
                        + dateEvent.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE);
                // ex. Vendredi 13 Mars

                eventLore.add(Component.text("- ")
                        .append(phase.getName())
                        .append(Component.text(" le " + formattedDate + " à "
                                + phase.getStartHour() + "h" + String.format("%02d", phase.getStartMinutes()))) // ex. 0h00
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                );
            }
        }
        return eventLore;
    }

    @Override
    public @NotNull String getName() {
        return "Calendrier Evenementiel";
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

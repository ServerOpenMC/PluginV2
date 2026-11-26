package fr.openmc.core.features.events.contents.christmas.mecanism.advent.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.events.contents.christmas.mecanism.advent.AdventCalendar;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdventCalendarMenu extends Menu {
    public AdventCalendarMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("empty");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-46::advent_calendar:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> items = new HashMap<>();

        for (AdventCalendar adventSlot : AdventCalendar.values()) {
            ItemMenuBuilder itemAdvent = adventSlot.getGeneratedItem(this);

            for (Integer slot : adventSlot.getSlots()) {
                items.put(slot, itemAdvent);
            }
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}

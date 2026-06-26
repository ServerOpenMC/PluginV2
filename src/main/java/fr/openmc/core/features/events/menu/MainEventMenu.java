package fr.openmc.core.features.events.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.events.EventsManager;
import fr.openmc.core.features.events.models.Event;
import fr.openmc.core.features.events.models.HasMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainEventMenu extends PaginatedMenu {

    public MainEventMenu(Player owner) {
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

        for (Event event : EventsManager.getAllEventsRegistred()) {
            items.add(new ItemMenuBuilder(this, event.getIcon(), meta -> {
                meta.displayName(event.getName());

                List<Component> lore = new ArrayList<>();

                lore.add(EventsManager.getEventTypeName(event));

                lore.addAll(event.getDescription());
                if (event instanceof HasMenu) {
                    lore.add(TranslationManager.translation("feature.events.menu.main_event.event.click_here"));
                }

                meta.lore(lore);
            }).setOnClick(_ -> {
                if (event instanceof HasMenu menu) {
                    menu.getInfoMenu(getOwner()).open();
                }
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
        return EventsManager.getAllEventsRegistred().size();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(27, new ItemMenuBuilder(this, Material.CLOCK, meta -> {
            meta.displayName(TranslationManager.translation("feature.events.calendar.title"));
            meta.lore(TranslationManager.translationLore("feature.events.menu.main_event.title.calendar.lore"));
        }).setOnClick(_ ->
                new CalendarMenu(getOwner()).open()));

        map.put(30, ItemMenuTemplate.BTN_PREVIOUS_PAGE_WHITE.apply(this));
        map.put(31, ItemMenuTemplate.BTN_CANCEL.apply(this));
        map.put(32, ItemMenuTemplate.BTN_NEXT_PAGE_WHITE.apply(this));
        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.menu.main_event.title");
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

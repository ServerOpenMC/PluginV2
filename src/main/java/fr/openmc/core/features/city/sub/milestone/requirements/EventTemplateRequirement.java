package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.EventCityRequirement;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class EventTemplateRequirement implements EventCityRequirement {
    private final BiPredicate<City, String> eqBool;
    private final Function<City, ItemStack> item;
    private final TriFunction<City, CityLevels, String, Component> name;
    private final String scope;
    private final Class<? extends Event> eventClass;
    private final BiConsumer<? super Event, String> onTrigger;

    public EventTemplateRequirement(
            BiPredicate<City, String> isDone,
            Function<City, ItemStack> item,
            TriFunction<City, CityLevels, String, Component> name,
            String scope,
            Class<? extends Event> eventClass,
            BiConsumer<? super Event, String> onTrigger
    ) {
        this.eqBool = isDone;
        this.item = item;
        this.name = name;
        this.scope = scope;
        this.eventClass = eventClass;
        this.onTrigger = onTrigger;
    }

    @Override
    public boolean isPredicateDone(City city) {
        return eqBool.test(city, getScope());
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public ItemStack getIcon(City city) {
        return item.apply(city);
    }

    @Override
    public Component getName(City city, CityLevels level) {
        return name.apply(city, level, getScope());
    }

    @Override
    public Component getDescription() {
        return null;
    }

    @EventHandler
    public void onEvent(Event event) {
        if (!eventClass.isInstance(event)) return;
        if (onTrigger != null) {
            onTrigger.accept(event, getScope());
        }
    }
}

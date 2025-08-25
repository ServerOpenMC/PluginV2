package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class TemplateRequirement implements CityRequirement {
    private final Predicate<City> eqBool;
    private final Function<City, ItemStack> item;
    private final BiFunction<City, CityLevels, Component> name;

    public TemplateRequirement(Predicate<City> isDone, Function<City, ItemStack> item, BiFunction<City, CityLevels, Component> name) {
        this.eqBool = isDone;
        this.item = item;
        this.name = name;
    }

    @Override
    public boolean isPredicateDone(City city) {
        return eqBool.test(city);
    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public ItemStack getIcon(City city) {
        return item.apply(city);
    }

    @Override
    public Component getName(City city, CityLevels level) {
        return name.apply(city, level);
    }

    @Override
    public Component getDescription() {
        return null;
    }
}

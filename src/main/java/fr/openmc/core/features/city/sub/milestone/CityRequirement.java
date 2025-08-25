package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.features.city.City;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public interface CityRequirement {
    boolean isPredicateDone(City city);

    default boolean isDone(City city, CityLevels level) {
        if (city.getLevel() > level.ordinal()) {
            return true;
        }
        return isPredicateDone(city);
    }

    String getScope();

    ItemStack getIcon(City city);

    Component getName(City city, CityLevels level);

    Component getDescription();
}

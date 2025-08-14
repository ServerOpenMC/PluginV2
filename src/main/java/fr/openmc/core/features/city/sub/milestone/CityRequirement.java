package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.features.city.City;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public interface CityRequirement {
    boolean isDone(City city);

    ItemStack getIcon();

    Component getName();

    Component getDescription();
}

package fr.openmc.core.features.events.models;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Event {
    public abstract Component getName();
    public abstract List<Component> getDescription();
    public abstract ItemStack getIcon();

}

package fr.openmc.core.features.events.weeklyevents.models;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class WeeklyEvent {
    public abstract Component getName();
    public abstract List<Component> getDescription();
    public abstract ItemStack getIcon();
    public abstract List<WeeklyEventPhase> getPhases();
}

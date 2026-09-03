package fr.openmc.core.registry.poi.interfaces;

import fr.openmc.core.events.RegionEnterEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public interface HasDetectionEnter extends Listener {
    Title getTitleEnter();

    // * a @Override
    default void onRegionEnter(RegionEnterEvent event) {}
}

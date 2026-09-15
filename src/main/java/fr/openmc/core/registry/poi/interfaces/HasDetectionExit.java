package fr.openmc.core.registry.poi.interfaces;

import fr.openmc.core.events.RegionLeaveEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.event.Listener;

public interface HasDetectionExit extends Listener {
    Title getTitleExit();

    // * a @Override
    default void onRegionExit(RegionLeaveEvent event) {}
}

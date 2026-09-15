package fr.openmc.core.registry.poi.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.events.RegionEnterEvent;
import fr.openmc.core.events.RegionLeaveEvent;
import fr.openmc.core.registry.poi.CustomPoi;
import fr.openmc.core.registry.poi.interfaces.HasDetectionEnter;
import fr.openmc.core.registry.poi.interfaces.HasDetectionExit;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class PoiDetectionListener implements Listener {
    @EventHandler
    public void onEnter(RegionEnterEvent event) {
        Optional<CustomPoi> poi = OMCRegistry.CUSTOM_POI.get(event.getRegion().getId());

        if (poi.isPresent() && poi.get() instanceof HasDetectionEnter detection) {
            Title title = detection.getTitleEnter();
            showTitle(event.getPlayer(), title);
            detection.onRegionEnter(event);
        }
    }

    @EventHandler
    public void onExit(RegionLeaveEvent event) {
        Optional<CustomPoi> poi = OMCRegistry.CUSTOM_POI.get(event.getRegion().getId());

        if (poi.isPresent() && poi.get() instanceof HasDetectionExit detection) {
            Title title = detection.getTitleExit();
            showTitle(event.getPlayer(), title);
            detection.onRegionExit(event);
        }
    }

    private void showTitle(Player player, Title title) {
        player.sendTitlePart(TitlePart.TITLE, title.title());
        player.sendTitlePart(TitlePart.SUBTITLE, title.subtitle());
        Title.Times times = title.times();
        if (times != null)
            player.sendTitlePart(TitlePart.TIMES, times);
    }
}

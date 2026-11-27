package fr.openmc.core.features.events.contents.christmas.listeners;

import fr.openmc.core.features.events.contents.christmas.contents.dailyevent.BlizzardEvent;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.registry.structures.model.CustomStructureManager;
import fr.openmc.core.registry.structures.model.DBPlacedStructure;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof BlizzardEvent)) return;

        RiftManager.getRifts().forEach(uuid -> {
            try {
                DBPlacedStructure structure = CustomStructureManager.getPlacement(uuid);

                event.getPlayer().sendMessage(TranslationManager.translation("feature.dailyevents.blizzard.rift.summoned",
                        Component.text(structure.getX()).color(NamedTextColor.AQUA),
                        Component.text(structure.getY()).color(NamedTextColor.AQUA),
                        Component.text(structure.getZ()).color(NamedTextColor.AQUA)
                ));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

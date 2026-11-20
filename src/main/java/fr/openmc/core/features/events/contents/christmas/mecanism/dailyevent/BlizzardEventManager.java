package fr.openmc.core.features.events.contents.christmas.mecanism.dailyevent;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.LocationUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class BlizzardEventManager extends Feature {
    public static void start() {
        spawnRifts();
    }

    public static void stop() {
        removeRifts();
    }

    private static void spawnRifts() {
        for (int i = 0; i < RandomUtils.randomBetween(2, 4); i++) {
            Location rift = LocationUtils.randomLocationInWorld(OMCRegistry.DAILY_EVENTS.BLIZZARD.getWorld());

            rift.setY(RandomUtils.randomBetween(140, 200));

            RiftManager.placeRift(rift);

            Bukkit.broadcast(TranslationManager.translation("feature.dailyevents.blizzard.rift.summoned",
                    Component.text(rift.getBlockX()).color(NamedTextColor.AQUA),
                    Component.text(rift.getBlockY()).color(NamedTextColor.AQUA),
                    Component.text(rift.getBlockZ()).color(NamedTextColor.AQUA)
            ));
        }
    }

    private static void removeRifts() {
        for (UUID riftUUID : RiftManager.getRifts()) {
            RiftManager.removeRift(riftUUID);
        }
    }
}

package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingManager;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

public class PlayerFishListener implements Listener {

    @EventHandler
    public void onStartFishing(PlayerFishEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof MiraculousFishingEvent)) return;

        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        MiraculousFishingManager.applyFishingSpeedModifier(hook);

        switch (event.getState()) {
            case FISHING -> {
                // * SFX
                // todo sfx
                player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 1f, 0.3f);
            }

            case CAUGHT_FISH -> {
                Entity caughtEntity = event.getCaught();
                if (caughtEntity instanceof Item caughtItem) {
                    caughtItem.remove();
                }

                CustomLootTable fishingLootTable = OMCRegistry.CUSTOM_LOOT_TABLES.MIRACULOUS_FISHING;

                List<CustomLoot> loots = fishingLootTable.rollLoots(player, false);

                // * SFX
                // todo: sfx particle
                for (CustomLoot loot : loots) {
                    MiraculousFishingManager.simulateLaunchLoot(player, hook.getLocation(), loot);
                }
            }
        }
    }
}

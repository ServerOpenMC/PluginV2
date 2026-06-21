package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingManager;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.MethodLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.loots.TableLoot;
import fr.openmc.core.utils.RngUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
                player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 1f, 0.7f);
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
                MessagesManager.sendMessage(player, TranslationManager.translation(
                        "feature.dailyevents.miraculousfishing.loottable.get",
                        Component.text(loots.size()).color(NamedTextColor.YELLOW)
                ), Prefix.MIRACULOUS_FISHING, MessageType.INFO, false);

                sendLoot(player, hook, loots);
            }
        }
    }

    private void sendLoot(Player player, FishHook hook, Collection<CustomLoot> loots) {
        for (CustomLoot loot : loots) {
            RngUtils.sendSoundRng(player, loot.getChance());

            MiraculousFishingManager.simulateLaunchLoot(player, hook.getLocation(), loot);

            // * Si y'a des sous loots, alors on affiche les sous loots obtenu,
            // en modiant leur probabilité corresponde à la réalité
            if (loot instanceof TableLoot) {
                Set<CustomLoot> subLoots = loot.run(player);

                for (CustomLoot subLoot : subLoots) {
                    subLoot.setChance(loot.getChance() * subLoot.getChance());
                }

                sendLoot(player, hook, subLoots);
            }
            // * Si c'est un loot de type MoneyLoot ou MethodLoot,
                // on exécute le loot, car on ne le donne va via un item
            else if (loot instanceof MoneyLoot || loot instanceof MethodLoot)
                loot.run(player);
        }
    }
}

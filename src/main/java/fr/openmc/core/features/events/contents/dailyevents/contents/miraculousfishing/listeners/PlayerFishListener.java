package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners;

import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.FishingAttributeManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.minigame.FishingMiniGameManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.minigame.FishingMiniGameResult;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.MethodLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.loots.TableLoot;
import fr.openmc.core.utils.RngUtils;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.event.entity.FishHookStateChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class PlayerFishListener implements Listener {

    @EventHandler
    public void onStartFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        boolean miraculousFishingActive = DailyEventsManager.isActiveDailyEvent()
                && DailyEventsManager.getActiveDailyEvent() instanceof MiraculousFishingEvent;

        if (miraculousFishingActive) {
            FishingAttributeManager.applyFishingSpeedModifier(player, hook);
        }

        if (event.isCancelled() || event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        if (FishingMiniGameManager.isActive(player)) {
            return;
        }

        Entity caughtEntity = event.getCaught();
        if (!(caughtEntity instanceof Item caughtItem)) {
            return;
        }

        ItemStack vanillaReward = caughtItem.getItemStack().clone();
        Location hookLocation = hook.getLocation().clone();

        boolean started = FishingMiniGameManager.start(player, result ->
                handleFishingReward(player, hookLocation, vanillaReward, miraculousFishingActive, result));

        if (!started) {
            return;
        }

        caughtItem.remove();

        ParticleUtils.spawnDispersingParticles(
                player,
                Particle.CLOUD,
                hookLocation,
                35,
                0.1D,
                null
        );
    }

    @EventHandler
    public void onHookOnWater(FishHookStateChangeEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof MiraculousFishingEvent)) {
            return;
        }

        Entity hook = event.getEntity();
        World world = hook.getWorld();

        if (event.getNewHookState() == FishHook.HookState.BOBBING) {
            ParticleUtils.sendParticlePacket(
                    hook.getLocation().getNearbyEntitiesByType(Player.class, 30),
                    Particle.POOF,
                    hook.getLocation(),
                    5,
                    0.1,
                    0.1,
                    0.1,
                    0.01,
                    null
            );
            world.playSound(hook.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 2f, 0.7f);
        }
    }

    private void handleFishingReward(
            Player player,
            Location hookLocation,
            ItemStack vanillaReward,
            boolean miraculousFishingActive,
            FishingMiniGameResult result
    ) {
        if (miraculousFishingActive) {
            giveMiraculousFishingReward(player, hookLocation, result);
            return;
        }

        giveVanillaFishingReward(player, vanillaReward, result);
    }

    private void giveMiraculousFishingReward(
            Player player,
            Location hookLocation,
            FishingMiniGameResult result
    ) {
        List<CustomLoot> loots = FishingAttributeManager.rollFishingLoots(result);
        List<CustomLoot> finalLoots = FishingAttributeManager.applyDoubleHookChance(player, loots);

        MessagesManager.sendMessage(
                player,
                TranslationManager.translation(
                        "feature.dailyevents.miraculousfishing.loot_table.get",
                        Component.text(finalLoots.size()).color(NamedTextColor.YELLOW)
                ),
                Prefix.MIRACULOUS_FISHING,
                MessageType.INFO,
                false
        );

        if (loots.size() * 2 == finalLoots.size()) {
            player.sendMessage(TranslationManager.translation(
                    "feature.dailyevents.miraculousfishing.loot_table.get.double_hook"
            ));
        }

        player.sendMessage(TranslationManager.translation(
                switch (result) {
                    case PERFECT -> "feature.dailyevents.miraculousfishing.minigame.result.perfect";
                    case GOOD -> "feature.dailyevents.miraculousfishing.minigame.result.good";
                    case MISS -> "feature.dailyevents.miraculousfishing.minigame.result.miss";
                }
        ));

        sendLoot(player, hookLocation, finalLoots);
    }

    private void giveVanillaFishingReward(
            Player player,
            ItemStack reward,
            FishingMiniGameResult result
    ) {
        player.sendMessage(TranslationManager.translation(
                switch (result) {
                    case PERFECT -> "feature.dailyevents.miraculousfishing.minigame.result.perfect";
                    case GOOD -> "feature.dailyevents.miraculousfishing.minigame.result.good";
                    case MISS -> "feature.dailyevents.miraculousfishing.minigame.result.miss";
                }
        ));

        var leftovers = player.getInventory().addItem(reward);
        leftovers.values().forEach(item ->
                player.getWorld().dropItemNaturally(player.getLocation(), item)
        );
    }

    private void sendLoot(Player player, Location hookLocation, Collection<CustomLoot> loots) {
        for (CustomLoot loot : loots) {
            RngUtils.sendSoundRng(player, loot.getChance());
            MiraculousFishingManager.simulateLaunchLoot(player, hookLocation, loot);

            if (loot instanceof TableLoot) {
                List<CustomLoot> subLoots = loot.run(player).loots();

                for (CustomLoot subLoot : subLoots) {
                    subLoot.setChance(loot.getChance() * subLoot.getChance());
                }

                sendLoot(player, hookLocation, subLoots);
            } else if (loot instanceof MoneyLoot || loot instanceof MethodLoot) {
                loot.run(player);
            }
        }
    }
}

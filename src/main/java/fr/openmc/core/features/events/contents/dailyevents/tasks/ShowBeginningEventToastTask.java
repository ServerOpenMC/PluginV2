package fr.openmc.core.features.events.contents.dailyevents.tasks;

import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.utils.nms.toast.ToastUtils;
import fr.openmc.core.utils.world.WorldUtils;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ShowBeginningEventToastTask extends BukkitRunnable {
    @Override
    public void run() {
        ScheduleDailyEvent nextEvent = DailyEventsManager.incomingEvents.getFirst();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String translationKey;
            Object[] translationsArgs;
            if (nextEvent.getDailyEvent().getWorldEvent().contains(onlinePlayer.getWorld().getName())) {
                translationKey = "feature.dailyevents.toast.beginning_event_in_world";
                translationsArgs = new Object[] {
                        DailyEventsManager.SHOW_BEGINNING_TOAST_DELAY
                };
            } else {
                translationKey = "feature.dailyevents.toast.beginning_event_out_world";
                translationsArgs = new Object[] {
                        Component.translatable(WorldUtils.getDisplayedWorldName(nextEvent.getDailyEvent().getWorldEvent())),
                        DailyEventsManager.SHOW_BEGINNING_TOAST_DELAY
                };
            }

            ToastUtils.sendCustomToast(
                    onlinePlayer,
                    Material.NOTE_BLOCK,
                    translationKey,
                    translationsArgs,
                    AdvancementType.TASK
            );
        }
    }
}

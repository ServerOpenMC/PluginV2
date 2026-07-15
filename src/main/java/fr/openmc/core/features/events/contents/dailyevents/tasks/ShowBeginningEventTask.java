package fr.openmc.core.features.events.contents.dailyevents.tasks;

import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.utils.nms.toast.ToastUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ShowBeginningEventTask extends BukkitRunnable {
    @Override
    public void run() {
        ScheduleDailyEvent nextEvent = DailyEventsManager.incomingEvents.getFirst();
        DailyEvent dailyEvent = nextEvent.getDailyEvent();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Component name;

            // ** Affichage d'un message dans le chat
            if (dailyEvent.getWorldEvent().contains(onlinePlayer.getWorld().getName())) {
                onlinePlayer.sendMessage(
                        TranslationManager.translation("feature.dailyevents.broadcast.soon"));
            }

            // ** Affichage du Toast
            if (dailyEvent.getWorldEvent().contains(onlinePlayer.getWorld().getName())) {
                name = TranslationManager.translation("feature.dailyevents.toast.beginning_event_in_world",
                        Component.text(DailyEventsManager.SHOW_BEGINNING_DELAY, NamedTextColor.YELLOW));
            } else {
                name = TranslationManager.translation("feature.dailyevents.toast.beginning_event_out_world",
                        TranslationManager.translation(WorldUtils.getDisplayedWorldName(dailyEvent.getWorldEvent())),
                        Component.text(DailyEventsManager.SHOW_BEGINNING_DELAY, NamedTextColor.YELLOW));
            }

            ToastUtils.sendCustomToast(
                    onlinePlayer,
                    Material.NOTE_BLOCK,
                    name,
                    AdvancementType.TASK
            );
        }
    }
}

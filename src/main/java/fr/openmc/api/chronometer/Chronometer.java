package fr.openmc.api.chronometer;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Chronometer{

    // Map structure: UUID -> (Group -> Time)
    public static final HashMap<UUID, HashMap<String, Integer>> chronometer = new HashMap<>();
    // Map structure: UUID -> Group -> Task
    private static final HashMap<UUID, HashMap<String, BukkitRunnable>> activeTasks = new HashMap<>();
    // new @EventHandler > ChronometerEndEvent

    @Getter
    public static class ChronometerEndEvent extends Event {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Entity entity;
        private final String group;

        public ChronometerEndEvent(Entity entity, String group) {
            this.entity = entity;
            this.group = group;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * FOR "start":
     * put "%sec%" in your message to display the remaining time
     * otherwise the default message will be displayed
     * the display time is in second
     * FOR "start" / "stopAll" / "stop":
     * if you don't want to display a message, just put "%null%"

     * @param entity entity to add
     * @param group Chronometer group
     * @param time duration in second
     * @param messageType display type
     * @param message to display the time
     * @param finishMessageType display type
     * @param finishMessage message display when the chronometer ends normally
     */
    public static void startChronometer(
            Entity entity,
            String group,
            int time,
            ChronometerType messageType,
            Component message,
            ChronometerType finishMessageType,
            Component finishMessage
    ) {
        UUID entityUUID = entity.getUniqueId();
        chronometer.computeIfAbsent(entityUUID, k -> new HashMap<>()).put(group, time);

        if (activeTasks.containsKey(entityUUID) && activeTasks.get(entityUUID).containsKey(group)) {
            activeTasks.get(entityUUID).get(group).cancel();
        }

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!chronometer.containsKey(entityUUID)) {
                    cancel();
                    return;
                }

                int remainingTime = chronometer.get(entityUUID).get(group);

                if (message != null && entity instanceof Player player) {
                    String timerMessage = PlainTextComponentSerializer.plainText().serialize(message)
                            .replace("%sec%", String.valueOf(remainingTime));

                    sendMessage(player, messageType, Component.text(timerMessage));
                }

                if (timerEnd(entityUUID, group)) {

                    if (entity instanceof Player player && finishMessage != null) {
                        sendMessage(player, finishMessageType, finishMessage);
                    }

                    Bukkit.getPluginManager().callEvent(new ChronometerEndEvent(entity, group));

                    if (chronometer.containsKey(entityUUID)) {
                        chronometer.get(entityUUID).remove(group);
                        if (chronometer.get(entityUUID).isEmpty()) {
                            chronometer.remove(entityUUID);
                        }
                    }
                    cancel();
                    return;
                }

                chronometer.get(entityUUID).put(group, remainingTime - 1);
            }
        };
        task.runTaskTimer(OMCPlugin.getInstance(), 0, 20);
        activeTasks.computeIfAbsent(entityUUID, k -> new HashMap<>()).put(group, task);
    }

    /**
     * @param entity entity who is affected
     * @param messageType display type
     * @param message message display when the chronometer is stopped
     */
    public static void stopAllChronometer(Entity entity, ChronometerType messageType, Component message) {
        UUID entityUUID = entity.getUniqueId();

        if (chronometer.containsKey(entityUUID)) {
            chronometer.remove(entityUUID);

            if (entity instanceof Player player && message != null) {
                sendMessage(player, messageType, message);
            }
        }

        if (activeTasks.containsKey(entityUUID)) {
            for (BukkitRunnable runnable : activeTasks.get(entityUUID).values()) {
                runnable.cancel();
            }
            activeTasks.remove(entityUUID);
        }
    }

    /**
     * @param entity entity who is affected
     * @param group Chronometer group
     * @param messageType display type
     * @param message message display when the chronometer is stopped
     */
    public static void stopChronometer(Entity entity, String group, ChronometerType messageType, Component message) {
        UUID entityUUID = entity.getUniqueId();

        if (chronometer.containsKey(entityUUID) && chronometer.get(entityUUID).containsKey(group)) {
            chronometer.get(entityUUID).remove(group);

            if (activeTasks.containsKey(entityUUID) && activeTasks.get(entityUUID).containsKey(group)) {
                activeTasks.get(entityUUID).get(group).cancel();
                activeTasks.get(entityUUID).remove(group);
            }

            if (entity instanceof Player player && message != null) {
                sendMessage(player, messageType, message);
            }

            if (chronometer.get(entityUUID).isEmpty()) {
                chronometer.remove(entityUUID);
            }
        } else {
            if (entity instanceof Player player) {
                MessagesManager.sendMessage(player,
                        TranslationManager.translation("api.chronometer.chronometer_not_found",
                                Component.text(group).color(NamedTextColor.GOLD)), Prefix.OPENMC, MessageType.INFO, false);
            }
        }
    }

    public static void listChronometers(Entity entity, Player owner) {
        UUID entitytUUID = entity.getUniqueId();

        if (chronometer.containsKey(entitytUUID)) {
            owner.sendMessage(TranslationManager.translation("api.chronometer.chronometer_on"));
            chronometer.get(entitytUUID).forEach((group, time) ->
                    owner.sendMessage(TranslationManager.translation("api.chronometer.chronometer_on_list",
                            Component.text(group), Component.text(time).color(NamedTextColor.GOLD)).color(NamedTextColor.YELLOW)));
        } else {
            owner.sendMessage(TranslationManager.translation("api.chronometer.none_chronometer_player"));
        }
    }

    /**
     * @return the remaining time
     */
    public static int getRemainingTime(UUID entityUUID, String group){
        return chronometer.get(entityUUID).get(group);
    }

    /**
     * @return true if the chronometer has expired
     */
    public static boolean timerEnd(UUID entityUUID, String group){
        return chronometer.get(entityUUID).get(group) <= 0;
    }

    public static boolean containsChronometer(UUID entityUUID, String group) {
        if (chronometer.containsKey(entityUUID)){
            return chronometer.get(entityUUID).containsKey(group);
        }
        return false;
    }

    private static void sendMessage(Player player, ChronometerType type, Component content) {
        if (Objects.requireNonNull(type) == ChronometerType.CHAT) {
            MessagesManager.sendMessage(player, content, Prefix.OPENMC, MessageType.INFO, false);
        } else {
            player.sendActionBar(content);
        }
    }
}

package fr.openmc.core.utils.chronometer;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Chronometer{

    // Map structure: UUID -> (Group -> Time)
    public static final HashMap<UUID, HashMap<String, Integer>> chronometer = new HashMap<>();
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
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * FOR "start" :
     * put "%sec%" in your message to display the remaining time
     * otherwise the default message will be displayed
     * the display time is in second

     * FOR "start" / "stopAll" / "stop" :
     * if you don't want to display a message just put "%null%"

     * @param entity entity to add
     * @param group Chronometer group
     * @param time duration in second
     * @param messageType display type
     * @param message to display the time
     * @param finishMessageType display type
     * @param finishMessage message display when the chronometer end normally
     */
    public static void startChronometer(Entity entity, String group, int time, ChronometerType messageType, String message, ChronometerType finishMessageType, String finishMessage) {
        UUID entityUUID = entity.getUniqueId();
        chronometer.computeIfAbsent(entityUUID, k -> new HashMap<>()).put(group, time);


        new BukkitRunnable() {
            @Override
            public void run() {

                if (!chronometer.containsKey(entityUUID)) {
                    cancel();
                    return;
                }

                int remainingTime = chronometer.get(entityUUID).get(group);
                String timerMessage = "Il reste : " + remainingTime + "s";
                if (message!=null){
                    if (!message.contains("%null%")){
                        if (message.contains("%sec%")) {
                            timerMessage = message.replace("%sec%", String.valueOf(remainingTime));
                        }
                        if (entity instanceof Player player){
                            player.spigot().sendMessage(messageType.getChatMessageType(),new TextComponent(timerMessage));
                        }
                    }
                } else {
                    if (entity instanceof Player player){
                        player.spigot().sendMessage(messageType.getChatMessageType(),new TextComponent(timerMessage));
                    }
                }


                if (timerEnd(entityUUID, group)) {
                    if (entity instanceof Player player){
                        player.spigot().sendMessage(finishMessageType.getChatMessageType(), new TextComponent(finishMessage != null ? finishMessage : "Le chronomètre est terminé !"));
                    }
                    Bukkit.getPluginManager().callEvent(new ChronometerEndEvent(entity, group));
                    chronometer.get(entityUUID).remove(group);
                    if (chronometer.get(entityUUID).isEmpty()){
                        chronometer.remove(entityUUID);
                    }
                    cancel();
                    return;
                }

                chronometer.get(entityUUID).put(group, remainingTime - 1);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 20);
    }

    /**
     * @param entity entity who is affect
     * @param messageType display type
     * @param message message display when the chronometer is stopped
     */
    public static void stopAllChronometer(Entity entity, ChronometerType messageType, String message) {
        UUID entityUUID = entity.getUniqueId();
        if (chronometer.containsKey(entityUUID)) {
            chronometer.remove(entityUUID);
            if (message!=null){
                if (!message.contains("%null%")){
                    if (entity instanceof Player player){
                        player.spigot().sendMessage(messageType.getChatMessageType(), new TextComponent(message));
                    }
                }
            } else {
                if (entity instanceof Player player){
                    player.spigot().sendMessage(messageType.getChatMessageType(), new TextComponent("Chronomètre arrêté"));
                }
            }
        }
    }

    /**
     * @param entity entity who is affect
     * @param group Chronometer group
     * @param messageType display type
     * @param message message display when the chronometer is stopped
     */
     public static void stopChronometer(Entity entity, String group, ChronometerType messageType, String message) {
        UUID entityUUID = entity.getUniqueId();

        if (chronometer.containsKey(entityUUID) && chronometer.get(entityUUID).containsKey(group)) {
            chronometer.get(entityUUID).remove(group);
            if (message!=null){
                if (!message.contains("%null%")){
                    if (entity instanceof Player player){
                        player.spigot().sendMessage(messageType.getChatMessageType(), new TextComponent(message));
                    }
                }
            } else {
                if (entity instanceof Player player){
                    player.spigot().sendMessage(messageType.getChatMessageType(), new TextComponent("Chronomètre du " + group + " arrêté"));
                }
            }

            if (chronometer.get(entityUUID).isEmpty()) {
                chronometer.remove(entityUUID);
            }
        } else {
            if (entity instanceof Player player){
                player.sendMessage("§cAucun chronomètre trouvé pour le groupe §e" + group + ".");
            }
        }
    }

    public static void listChronometers(Entity entity, Player owner) {
        UUID entitytUUID = entity.getUniqueId();

        if (chronometer.containsKey(entitytUUID)) {
            owner.sendMessage("§aChronomètres actifs :");
            chronometer.get(entitytUUID).forEach((group, time) ->
                    owner.sendMessage(" §e- " + group + ": §6" + time + "s")
            );
        } else {
            owner.sendMessage("§cCe joueur n'a aucun chronomètre actif.");
        }
    }

    /**
     * @return the remaining time
     */
    public static int getRemainingTime(UUID entityUUID, String group){
        return chronometer.get(entityUUID).get(group);
    }

    /**
     * @return true if chronometer has expired
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
}

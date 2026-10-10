package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.chronometer.ChronometerType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class OMCPlayerChronometer extends OMCPlayerFeat {
    public OMCPlayerChronometer(Player player) {
        super(player);
    }

    public void startChronometer(
            String group,
            int time,
            ChronometerType messageType,
            String key,
            ChronometerType finishMessageType,
            Component finishMessage
    ) {
        Chronometer.startChronometer(getPlayer(), group, time, messageType, key, finishMessageType, finishMessage);
    }

    public void stopAllChronometer(ChronometerType messageType, Component message) {
        Chronometer.stopAllChronometer(getPlayer(), messageType, message);
    }

    public void stopChronometer(String group, ChronometerType messageType, Component message) {
        Chronometer.stopChronometer(getPlayer(), group, messageType, message);
    }

    public int getRemainingTime(String group) {
        return Chronometer.getRemainingTime(getUniqueId(), group);
    }

    public boolean timerEnd(String group) {
        return Chronometer.timerEnd(getUniqueId(), group);
    }

    public boolean containsChronometer(String group) {
        return Chronometer.containsChronometer(getUniqueId(), group);
    }
}


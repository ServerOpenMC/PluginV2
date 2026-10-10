package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.chronometer.ChronometerType;
import fr.openmc.api.cooldown.Cooldown;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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


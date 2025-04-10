package fr.openmc.core.features.city.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class CityTypeCooldown implements Listener {

    private static final long COOLDOWN_TIME = 5 * 24 * 60 * 60 * 1000L; // 5 jours en ms

    @EventHandler
    void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city!=null){
            String city_uuid = city.getUUID();
            if (!DynamicCooldownManager.isReady(city_uuid, "city:type")) {
                MessagesManager.sendMessage(player, Component.text("Type de ville changeable dans : " + DynamicCooldownManager.getRemaining(UUID.fromString(city_uuid).toString(), "city:type")/1000 + "s"), Prefix.CITY, MessageType.INFO, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Type de ville changeable"), Prefix.CITY, MessageType.INFO, false);
            }
        }
    }
}


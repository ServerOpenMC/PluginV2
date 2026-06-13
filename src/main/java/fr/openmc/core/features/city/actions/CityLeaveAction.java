package fr.openmc.core.features.city.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityLeaveCondition;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class CityLeaveAction {

    public static void startLeave(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        if (!CityLeaveCondition.canCityLeave(city, player)) return;

        city.removePlayer(player.getUniqueId());

        MessagesManager.sendMessage(player,
                TranslationManager.translation("feature.city.leave.success", Component.text(city.getName())),
                Prefix.CITY,
                MessageType.SUCCESS,
                false
        );

        city.getOnlineMembers().forEach(memberUUID -> {
            if (memberUUID.equals(player.getUniqueId())) return;
            Player onlineMember = Bukkit.getPlayer(memberUUID);
            MessagesManager.sendMessage(onlineMember,
                    TranslationManager.translation(
                            "feature.city.leave.info",
                            Component.text(player.getName()),
                            Component.text(city.getName())
                    ),
                    Prefix.CITY,
                    MessageType.INFO,
                    true
            );
        });
    }
}

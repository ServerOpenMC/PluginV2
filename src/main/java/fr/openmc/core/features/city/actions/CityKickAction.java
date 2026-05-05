package fr.openmc.core.features.city.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityKickCondition;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class CityKickAction {
    public static void startKick(Player sender, OfflinePlayer playerKick) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityKickCondition.canCityKickPlayer(city, sender, playerKick)) return;

        if (city == null) return;

        city.removePlayer(playerKick.getUniqueId());
        MessagesManager.sendMessage(sender,
                TranslationManager.translation(
                        "feature.city.kick.success",
                        PlayerNameCache.name(playerKick.getUniqueId()),
                        Component.text(city.getName())
                ),
                Prefix.CITY,
                MessageType.SUCCESS,
                false
        );

        if (playerKick.isOnline()) {
            MessagesManager.sendMessage((Player) playerKick,
                    TranslationManager.translation(
                            "feature.city.kick.info",
                            Component.text(city.getName())
                    ),
                    Prefix.CITY,
                    MessageType.INFO,
                    true
            );
        }
    }
}

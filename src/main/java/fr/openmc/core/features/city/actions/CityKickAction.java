package fr.openmc.core.features.city.actions;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.conditions.CityKickCondition;
import fr.openmc.core.utils.cache.CachePlayerName;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class CityKickAction {
    public static void startKick(OMCPlayer sender, OMCOfflinePlayer playerKick) {
        City city = sender.city().getCity();

        if (!CityKickCondition.canCityKickPlayer(city, sender, playerKick)) return;

        if (city == null) return;

        city.removePlayer(playerKick.getUniqueId());
        sender.message().sendSuccess(
                TranslationManager.translation(
                        "feature.city.kick.success",
                        CachePlayerName.name(playerKick.getUniqueId()),
                        Component.text(city.getName())
                ),
                Prefix.CITY,
                false
        );

       playerKick.message().sendInfo(
               TranslationManager.translation(
                       "feature.city.kick.info",
                       Component.text(city.getName())
               ),
               Prefix.CITY,
               true
       );
    }
}

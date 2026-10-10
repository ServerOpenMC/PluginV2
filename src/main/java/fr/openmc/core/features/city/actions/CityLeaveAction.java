package fr.openmc.core.features.city.actions;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.conditions.CityLeaveCondition;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;


public class CityLeaveAction {

    public static void startLeave(OMCPlayer player) {
        City city = player.city().getCity();

        if (city == null) return;

        if (!CityLeaveCondition.canCityLeave(city, player)) return;

        city.removePlayer(player.getUniqueId());

        player.message().sendSuccess(
                TranslationManager.translation("feature.city.leave.success",
                        Component.text(city.getName())),
                Prefix.CITY,
                false
        );

        city.getOnlineMembers().forEach(memberUUID -> {
            if (memberUUID.equals(player.getUniqueId())) return;
            OMCPlayer onlineMember = OMCPlayer.of(memberUUID);
            onlineMember.message().sendInfo(
                    TranslationManager.translation(
                            "feature.city.leave.info",
                            player.getNameWithHead(),
                            Component.text(city.getName())
                    ),
                    Prefix.CITY,
                    true
            );
        });
    }
}

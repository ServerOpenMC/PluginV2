package fr.openmc.core.features.city.sub.war.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.autocomplete.CityNameAutoComplete;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admwar")
@CommandPermission("omc.admins.commands.adminwar")
public class AdminWarCommand {
    @Subcommand("startCombat")
    @CommandPermission("omc.admins.commands.adminwar.startCombat")
    void startCombat(
            Player player,
            @Named("name") @SuggestWith(CityNameAutoComplete.class) String cityName
    ) {
        City city = CityManager.getCityByName(cityName);

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (!city.isInWar() && city.getWar().getPhase() != War.WarPhase.PREPARATION) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.war.admin.not_in_preparation"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.getWar().startCombat();
    }
}

package fr.openmc.core.features.city.sub.mascots.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.autocomplete.CityNameAutoComplete;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
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

@Command("admmascot")
@CommandPermission("omc.admins.commands.adminmascot")
public class AdminMascotsCommands {

    @Subcommand("remove")
    @CommandPermission("omc.admins.commands.adminmascot.remove")
    public void forceRemoveMascots(
            Player sender,
            @Named("cityName") @SuggestWith(CityNameAutoComplete.class) String cityName
    ) {
        City city = CityManager.getCityByName(cityName);

        if (city == null) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.not_found"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        MascotsManager.removeMascotsFromCity(city);
        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.mascots.admin.remove.success"), Prefix.CITY, MessageType.SUCCESS, false);
    }
}

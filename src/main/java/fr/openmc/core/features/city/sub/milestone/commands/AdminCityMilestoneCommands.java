package fr.openmc.core.features.city.sub.milestone.commands;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.autocomplete.CityNameAutoComplete;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AdminCityMilestoneCommands {
    @Command({"admcity milestone skipUpgrade"})
    @CommandPermission("omc.admins.commands.milestone")
    @Description("Skip l'upgrade d'un level")
    void adminSkinUpgrade(
            Player sender,
            @Named("cityName") @SuggestWith(CityNameAutoComplete.class) String cityName
    ) {
        City city = CityManager.getCityByName(cityName);

        if (city == null) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.levels.admin.skip_upgrade.success"), Prefix.STAFF, MessageType.SUCCESS, false);
        DynamicCooldownManager.clear(city.getUniqueId(), "city:upgrade-level", true);
    }

    @Command({"admcity milestone setlevel"})
    @CommandPermission("omc.admins.commands.milestone")
    @Description("Skip l'upgrade d'un level")
    void setLevel(
            Player sender,
            @Named("cityName") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("level") @Range(min = 1, max = 10) int level
    ) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.setLevel(level);
        MessagesManager.sendMessage(sender, TranslationManager.translation(
                "feature.city.levels.admin.set_level.success",
                Component.text(level)
        ), Prefix.STAFF, MessageType.SUCCESS, false);
    }

}

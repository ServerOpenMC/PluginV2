package fr.openmc.core.features.city.sub.notation.commands;

import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.menu.NotationEditionDialog;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

import static fr.openmc.core.features.city.sub.notation.NotationManager.calculateAllCityScore;
import static fr.openmc.core.features.city.sub.notation.NotationManager.giveReward;


public class AdminNotationCommands {
    @Command({"admcity notation edit"})
    @CommandPermission("omc.admins.commands.admcity.notation")
    public void editNotations(Player sender) {
	    Component exempleTip = TranslationManager.translation("feature.city.notation.admin.edit.example",
                Component.text(DateUtils.getWeekFormat()),
                Component.text(DateUtils.getNextWeekFormat()));
        DialogInput.send(sender, TranslationManager.translation("feature.city.notation.admin.edit.prompt",
                        exempleTip),
                7, weekStr -> {
                    if (weekStr == null || weekStr.isEmpty()) {
	                    MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.notation.admin.edit.invalid",
                                exempleTip), Prefix.STAFF, MessageType.ERROR, false);
                        return;
                    }

                    List<City> cities = CityManager.getCities()
                            .stream()
                            .filter(city -> FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.NOTATION))
                            .toList();

                    if (cities.isEmpty()) {
                        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.notation.admin.edit.none"), Prefix.STAFF, MessageType.ERROR, false);
                        return;
                    }

                    try {
                        NotationEditionDialog.send(sender, weekStr, cities, null);
                    } catch (Exception e) {
                        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.notation.admin.edit.error"), Prefix.STAFF, MessageType.ERROR, false);
                    }
                });
    }

    @Command({"admcity notation publish"})
    @CommandPermission("omc.admins.commands.admcity.notation")
    public void publishNotations(Player sender) {
        String weekStr = DateUtils.getWeekFormat();

        if (!NotationManager.notationPerWeek.containsKey(weekStr)) {
	        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.notation.admin.publish.missing",
                    Component.text(weekStr)), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        try {
            calculateAllCityScore(weekStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        giveReward(weekStr);
	    
	    MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.notation.admin.publish.success",
                Component.text(weekStr)), Prefix.STAFF, MessageType.ERROR, false);

    }
}

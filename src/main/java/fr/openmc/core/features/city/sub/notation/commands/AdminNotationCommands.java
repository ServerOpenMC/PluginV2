package fr.openmc.core.features.city.sub.notation.commands;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.menu.NotationEditionDialog;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;


public class AdminNotationCommands {
    private final CityManager cityManager;
    private final NotationManager notationManager;

    public AdminNotationCommands(CityManager cityManager) {
        this.cityManager = cityManager;
        this.notationManager = OMCRegistry.CITY_FEATURES.NOTATION;
    }

    @Command({"admcity notation edit"})
    @CommandPermission("omc.admins.commands.admcity.notation")
    public void editNotations(OMCPlayer sender) {
	    Component exempleTip = TranslationManager.translation("feature.city.notation.admin.edit.example",
                Component.text(DateUtils.getWeekFormat()),
                Component.text(DateUtils.getNextWeekFormat()));
        sender.inputs().sendStringDialogInput(TranslationManager.translation("feature.city.notation.admin.edit.prompt",
                        exempleTip),
                7, weekStr -> {
                    if (weekStr == null || weekStr.isEmpty()) {
	                    sender.message().sendError(TranslationManager.translation("feature.city.notation.admin.edit.invalid",
                                exempleTip), Prefix.STAFF, false);
                        return;
                    }

                    List<City> cities = cityManager.getCities()
                            .stream()
                            .filter(city -> FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.NOTATION))
                            .toList();

                    if (cities.isEmpty()) {
                        sender.message().sendError(TranslationManager.translation("feature.city.notation.admin.edit.none"), Prefix.STAFF, false);
                        return;
                    }

                    try {
                        NotationEditionDialog.send(sender, weekStr, cities, null);
                    } catch (Exception e) {
                        sender.message().sendError(TranslationManager.translation("feature.city.notation.admin.edit.error"), Prefix.STAFF, false);
                    }
                });
    }

    @Command({"admcity notation publish"})
    @CommandPermission("omc.admins.commands.admcity.notation")
    public void publishNotations(OMCPlayer sender) {
        String weekStr = DateUtils.getWeekFormat();

        if (!notationManager.notationPerWeek.containsKey(weekStr)) {
            sender.message().sendError(TranslationManager.translation("feature.city.notation.admin.publish.missing",
                    Component.text(weekStr)), Prefix.STAFF, false);
            return;
        }

        try {
            notationManager.calculateAllCityScore(weekStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        notationManager.giveReward(weekStr);
	    
	    sender.message().sendError(TranslationManager.translation("feature.city.notation.admin.publish.success",
                Component.text(weekStr)), Prefix.STAFF, false);

    }
}

package fr.openmc.core.features.city.sub.notation.commands;

import fr.openmc.api.input.DialogInput;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.menu.NotationEditionDialog;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
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
	    String exempleTip = "Exemple : cette semaine on est le " + DateUtils.getWeekFormat() + " et la semaine prochaine, le " + DateUtils.getNextWeekFormat();
        DialogInput.send(sender, Component.text("Entrer le format de la semaine (" + exempleTip + ")"),
                7, weekStr -> {
                    if (weekStr == null || weekStr.isEmpty()) {
	                    MessagesManager.sendMessage(sender, Component.text("Saisie fausse ! " + exempleTip), Prefix.STAFF, MessageType.ERROR, false);
                        return;
                    }

                    List<City> cities = CityManager.getCities()
                            .stream()
                            .filter(city -> FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.NOTATION))
                            .toList();

                    if (cities.isEmpty()) {
                        MessagesManager.sendMessage(sender, Component.text("Aucune ville a éditer"), Prefix.STAFF, MessageType.ERROR, false);
                        return;
                    }

                    try {
                        NotationEditionDialog.send(sender, weekStr, cities, null);
                    } catch (Exception e) {
                        MessagesManager.sendMessage(sender, Component.text("Erreur lors de l'ouverture du menu"), Prefix.STAFF, MessageType.ERROR, false);
                    }
                });
    }

    @Command({"admcity notation publish"})
    @CommandPermission("omc.admins.commands.admcity.notation")
    public void publishNotations(Player sender) {
        String weekStr = DateUtils.getWeekFormat();

        if (!NotationManager.notationPerWeek.containsKey(weekStr)) {
	        MessagesManager.sendMessage(sender, Component.text("Vous devez faire §6/admcity notation edit §ret éditer la semaine " + weekStr), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        try {
            calculateAllCityScore(weekStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        giveReward(weekStr);
	    
	    MessagesManager.sendMessage(sender, Component.text("La semaine " + weekStr + " a été publiée, les notes d'économies et d'activité ainsi que les gains ont été calculés et donnés"), Prefix.STAFF, MessageType.ERROR, false);

    }
}
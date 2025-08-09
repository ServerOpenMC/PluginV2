package fr.openmc.core.features.city.sub.notation.commands;

import fr.openmc.api.input.DialogInput;
import fr.openmc.core.features.city.CityManager;
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

import static fr.openmc.core.features.city.sub.notation.NotationManager.calculateAllCityScore;
import static fr.openmc.core.features.city.sub.notation.NotationManager.giveReward;


public class AdminNotationCommands {
    @Command({"admcity notation edit"})
    @CommandPermission("omc.admins.commands.admcity.notation")
    public void editNotations(Player sender) {
        String exempleTip = "Exemple : cette semaine on est le " + DateUtils.getWeekFormat() + " et la semaine prochaine " + DateUtils.getNextWeekFormat();
        DialogInput.send(sender, Component.text("Entrer le format de la semaine (" + exempleTip + ")"),
                7, weekStr -> {
                    if (weekStr == null || weekStr.isEmpty()) {
                        MessagesManager.sendMessage(sender, Component.text("Sasie fausse ! " + exempleTip), Prefix.STAFF, MessageType.ERROR, false);
                        return;
                    }

                    try {
                        NotationEditionDialog.send(sender, weekStr, CityManager.getCities().stream().toList(), null);
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
            MessagesManager.sendMessage(sender, Component.text("Vous devez faire /admcity notation edit et éditez la semaine " + weekStr), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        calculateAllCityScore(weekStr);

        giveReward(weekStr);

        MessagesManager.sendMessage(sender, Component.text("La semaine " + weekStr + " a été publié, les notes d'économies et d'activité ainsi que les gains ont été calculé et donné"), Prefix.STAFF, MessageType.ERROR, false);

    }
}
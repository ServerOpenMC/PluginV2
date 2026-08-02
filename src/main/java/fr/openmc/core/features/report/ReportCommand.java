package fr.openmc.core.features.report;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("report")
@CommandPermission("omc.commands.report")
public class ReportCommand {
    @Subcommand("player")
    @CommandPermission("omc.commands.report.player")
    public void reportPlayer(Player sender,
                             @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target) {
        // todo: mettre une condition si le joueur qui envoie la demande à un compte discord pour le contacter au cas ou

        ReportPlayerDialog.send(sender, target);
    }
}

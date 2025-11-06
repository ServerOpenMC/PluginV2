package fr.openmc.core.features.economy.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Pay {

    @Command("pay")
    @Description("Permet de payer un joueur")
    @CommandPermission("omc.commands.pay")
    public void pay(
            Player player,
            @Named("joueur") @SuggestWith(OnlinePlayerAutoComplete.class) Player target,
            @Named("montant") @Range(min = 1) double amount
    ) {
        if(player == target) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas vous payer vous-même"), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }
        if(EconomyManager.transferBalance(player.getUniqueId(), target.getUniqueId(), amount, "Paiement")) {
            MessagesManager.sendMessage(player, Component.text("§aVous avez payé §e" + target.getName() + "§a de §e" + EconomyManager.getFormattedNumber(amount)), Prefix.OPENMC, MessageType.SUCCESS, true);
            MessagesManager.sendMessage(target, Component.text("§aVous avez reçu §e" + EconomyManager.getFormattedNumber(amount) + "§a de §e" + player.getName()), Prefix.OPENMC, MessageType.INFO, true);
        } else {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas assez d'argent"), Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

}

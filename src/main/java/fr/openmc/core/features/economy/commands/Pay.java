package fr.openmc.core.features.economy.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.economy.pay.self"), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }
        if(EconomyManager.transferBalance(player.getUniqueId(), target.getUniqueId(), amount, "Paiement de " + player.getName() + " à " + target.getName())) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.economy.pay.success",
                            Component.text(target.getName()).color(NamedTextColor.YELLOW),
                            Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.SUCCESS, true);
            MessagesManager.sendMessage(target,
                    TranslationManager.translation(
                            "feature.economy.pay.received",
                            Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW),
                            Component.text(player.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.INFO, true);
        } else {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.economy.pay.not_enough"), Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

}

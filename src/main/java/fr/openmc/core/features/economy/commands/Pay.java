package fr.openmc.core.features.economy.commands;

import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Pay {

    @Command("pay")
    @Description("Permet de payer un joueur")
    @CommandPermission("omc.commands.pay")
    public void pay(
            OMCPlayer player,
            @Named("joueur") @SuggestWith(OnlinePlayerAutoComplete.class) OMCPlayer target,
            @Named("montant") @Range(min = 1) double amount
    ) {
        if (player.equals(target)) {
            player.message().sendError(TranslationManager.translation("feature.economy.pay.self"));
            return;
        }
        if (player.pay(target.getUniqueId(), amount, String.format("Paiement de %s à %s", player.getName(), target.getName()))) {
            player.message().sendSuccess(TranslationManager.translation(
                    "feature.economy.pay.success",
                    Component.text(target.getName()).color(NamedTextColor.YELLOW),
                    Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW)
            ));
            target.message().sendInfo(TranslationManager.translation(
                    "feature.economy.pay.received",
                    Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW),
                    Component.text(player.getName()).color(NamedTextColor.YELLOW)
            ));
        } else {
            player.message().sendError(TranslationManager.translation("feature.economy.pay.not_enough"));
        }
    }

}

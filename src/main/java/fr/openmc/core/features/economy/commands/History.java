package fr.openmc.core.features.economy.commands;

import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.economy.menu.TransactionsMenu;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class History {
    @Command("money history")
    @Description("Affiche votre historique de transactions")
    @CommandPermission("omc.commands.money.history")
    @Cooldown(30)
    public void history(
            OMCPlayer sender,
            @Named("joueur") @Optional @SuggestWith(OnlinePlayerAutoComplete.class) OMCPlayer target
    ) {
        if (target == null || !sender.hasPermission("omc.admin.money.history")) {
            target = sender;
        }

        sender.open(TransactionsMenu.class, target.getUniqueId());
    }
}

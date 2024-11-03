package fr.openmc.core.features.economy.commands;

import fr.openmc.core.features.economy.TransactionsMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class History {
    @Command("money history")
    @Description("Affiche votre historique de transactions")
    @CommandPermission("omc.commands.money.history")
    @Cooldown(30)
    public void history(Player sender, @Optional Player target){
        if (!(sender instanceof Player player)) { return; }

        if (target == null) {
            target = player;
        } else {
            if (!sender.hasPermission("omc.admin.money.history")) {
                target = player;
            }
        }

        new TransactionsMenu(player, target.getUniqueId()).open();
    }
}

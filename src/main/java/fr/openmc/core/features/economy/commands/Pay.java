package fr.openmc.core.features.economy.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.Transaction;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Pay {
    private final MessagesManager msgOMC  = new MessagesManager(Prefix.OPENMC);

    @Command("pay")
    @Description("Permet de payer un joueur")
    @CommandPermission("omc.commands.pay")
    public void pay(Player player, Player target, @Range(min = 1) double amount) {
        EconomyManager economyManager = EconomyManager.getInstance();
        if(player == target) {
            msgOMC.error(player, "§cVous ne pouvez pas vous payer vous-même");
            return;
        }
        if(economyManager.withdrawBalance(player.getUniqueId(), amount)) {
            economyManager.addBalance(target.getUniqueId(), amount);
            msgOMC.success(player, "§aVous avez payé §e" + target.getName() + "§a de §e" + economyManager.getFormattedNumber(amount));
            msgOMC.info(target, "§aVous avez reçu §e" + economyManager.getFormattedNumber(amount) + "§a de §e" + player.getName());

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                new Transaction(
                        target.getUniqueId().toString(),
                        player.getUniqueId().toString(),
                        amount,
                        "Paiement"
                ).register();
            });
        } else {
            msgOMC.error(player, "§cVous n'avez pas assez d'argent");
        }
    }

}

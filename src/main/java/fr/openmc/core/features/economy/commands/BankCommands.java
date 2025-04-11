package fr.openmc.core.features.economy.commands;

import org.bukkit.entity.Player;

import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.menu.PersonalBankMenu;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;

@Command({"bank", "banque"})
public class BankCommands {

    @DefaultFor("~")
    void openBankMenu(Player player) {
        new PersonalBankMenu(player).open();
    }

    @Subcommand("deposit")
    void deposit(Player player, String input) {
        BankManager.getInstance().addBankBalance(player, input);
    }

    @Subcommand("withdraw")
    void withdraw(Player player, String input) {
        BankManager.getInstance().withdrawBankBalance(player, input);
    }
}

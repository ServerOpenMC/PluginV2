package fr.openmc.core.features.economy.commands;

import org.bukkit.entity.Player;

import fr.openmc.core.features.economy.menu.PersonalBankMenu;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;

@Command({"bank", "banque"})
public class BankCommands {

    @DefaultFor("~")
    void openBankMenu(Player player) {
        new PersonalBankMenu(player).open();
    }
}

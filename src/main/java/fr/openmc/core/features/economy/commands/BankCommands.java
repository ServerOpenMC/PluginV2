package fr.openmc.core.features.economy.commands;

import org.bukkit.entity.Player;

import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.menu.PersonalBankMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({ "bank", "banque" })
public class BankCommands {

    @DefaultFor("~")
    @Description("Ouvre le menu de votre banque personelle")
    void openBankMenu(Player player) {
        new PersonalBankMenu(player).open();
    }

    @Subcommand("deposit")
    @Description("Ajout de l'argent a votre banque personelle")
    void deposit(Player player, String input) {
        BankManager.getInstance().addBankBalance(player, input);
    }

    @Subcommand("withdraw")
    @Description("Retire de l'argent de votre banque personelle")
    void withdraw(Player player, String input) {
        BankManager.getInstance().withdrawBankBalance(player, input);
    }

    @Subcommand({ "balance", "bal" })
    void withdraw(Player player) {
        double balance = BankManager.getInstance().getBankBalance(player.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("Il y a §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(balance) + "§r" + EconomyManager.getEconomyIcon() + " dans ta banque"), Prefix.BANK, MessageType.INFO, false);
    }

    @Subcommand("admin interest apply")
    @Description("Distribue les intérèts à tout les joueurs et a toute les villes")
    @CommandPermission("omc.admins.commands.bank.interest.apply")
    void applyInterest(Player player) {
        MessagesManager.sendMessage(player, Component.text("Distribution des intérèts en cours..."), Prefix.BANK, MessageType.INFO, false);
        BankManager.getInstance().applyAllPlayerInterests();
        CityManager.applyAllCityInterests();
        MessagesManager.sendMessage(player, Component.text("Distribution des intérèts réussie."), Prefix.BANK, MessageType.SUCCESS, false);
    }
}

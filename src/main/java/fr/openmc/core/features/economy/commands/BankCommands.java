package fr.openmc.core.features.economy.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.menu.PersonalBankMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({ "bank", "banque" })
public class BankCommands {

    @CommandPlaceholder()
    @Description("Ouvre le menu de votre banque personelle")
    public static void openBankMenu(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null || !FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.PLAYER_BANK)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK) + "!"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new PersonalBankMenu(player).open();
    }

    @Subcommand("deposit")
    @Description("Ajout de l'argent a votre banque personelle")
    void deposit(Player player, String input) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null || !FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.PLAYER_BANK)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK) + "!"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        BankManager.deposit(player.getUniqueId(), input);
    }

    @Subcommand("withdraw")
    @Description("Retire de l'argent de votre banque personelle")
    void withdraw(Player player, String input) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null || !FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.PLAYER_BANK)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK) + "!"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        BankManager.withdraw(player.getUniqueId(), input);
    }

    @Subcommand({ "balance", "bal" })
    void withdraw(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null || !FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.PLAYER_BANK)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK) + "!"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        double balance = BankManager.getBankBalance(player.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("Il y a §d" + EconomyManager.getFormattedSimplifiedNumber(balance) + "§r" + EconomyManager.getEconomyIcon() + " dans ta banque"), Prefix.BANK, MessageType.INFO, false);
    }

    @Subcommand("admin interest apply")
    @Description("Distribue les intérèts à tout les joueurs et a toute les villes")
    @CommandPermission("omc.admins.commands.bank.interest.apply")
    void applyInterest(Player player) {
        MessagesManager.sendMessage(player, Component.text("Distribution des intérêts en cours..."), Prefix.BANK, MessageType.INFO, false);
        BankManager.applyAllPlayerInterests();
        CityBankManager.applyAllCityInterests();
        MessagesManager.sendMessage(player, Component.text("Distribution des intérêts réussie."), Prefix.BANK, MessageType.SUCCESS, false);
    }
}

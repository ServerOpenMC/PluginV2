package fr.openmc.core.features.economy.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.menu.PersonalBankMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({ "bank", "banque" })
public class BankCommands {

    @CommandPlaceholder()
    @Description("Ouvre le menu de votre banque personelle")
    public static void openBankMenu(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null || !FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.PLAYER_BANK)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.economy.bank.command.not_unlocked",
                            Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new PersonalBankMenu(player).open();
    }

    @Subcommand("deposit")
    @Description("Ajout de l'argent a votre banque personelle")
    void deposit(
            Player player,
            @Named("montant") String input
    ) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null || !FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.PLAYER_BANK)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.economy.bank.command.not_unlocked",
                            Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        BankManager.deposit(player.getUniqueId(), input);
    }

    @Subcommand("withdraw")
    @Description("Retire de l'argent de votre banque personelle")
    void withdraw(
            Player player,
            @Named("montant") String input
    ) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null || !FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.PLAYER_BANK)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.economy.bank.command.not_unlocked",
                            Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.CITY, MessageType.ERROR, false);
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
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.economy.bank.command.balance",
                        Component.text(EconomyManager.getFormattedSimplifiedNumber(balance)).color(NamedTextColor.LIGHT_PURPLE),
                        Component.text(EconomyManager.getEconomyIcon())
                ),
                Prefix.BANK, MessageType.INFO, false);
    }

    @Subcommand("admin interest apply")
    @Description("Distribue les intérèts à tout les joueurs et a toute les villes")
    @CommandPermission("omc.admins.commands.bank.interest.apply")
    void applyInterest(Player player) {
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.economy.bank.interest.apply.start"), Prefix.BANK, MessageType.INFO, false);
        BankManager.applyAllPlayerInterests();
        CityBankManager.applyAllCityInterests();
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.economy.bank.interest.apply.success"), Prefix.BANK, MessageType.SUCCESS, false);
    }
}

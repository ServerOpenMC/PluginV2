package fr.openmc.core.features.economy.commands;

import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.menu.PersonalBankMenu;
import fr.openmc.core.features.economy.utils.EconomyUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"bank", "banque"})
public class BankCommands {
    private final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();
    private final BankManager bankManager = OMCRegistry.FEATURES.BANK.get();

    @CommandPlaceholder()
    @Description("Ouvre le menu de votre banque personelle")
    public static void openBankMenu(OMCPlayer player) {
        if (player.city().hasCity() || !FeaturesRewards.hasUnlockFeature(player.city().getCity(), FeaturesRewards.Feature.PLAYER_BANK)) {
            player.message().sendError(
                    TranslationManager.translation(
                            "feature.economy.bank.command.not_unlocked",
                            Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.CITY
            );
            return;
        }

        new PersonalBankMenu(player).open();
    }

    @Subcommand("deposit")
    @Description("Ajout de l'argent a votre banque personelle")
    void deposit(
            OMCPlayer player,
            @Named("montant") String input
    ) {
        if (player.city().hasCity() || !FeaturesRewards.hasUnlockFeature(player.city().getCity(), FeaturesRewards.Feature.PLAYER_BANK)) {
            player.message().sendError(
                    TranslationManager.translation(
                            "feature.economy.bank.command.not_unlocked",
                            Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.CITY
            );
            return;
        }

        bankManager.deposit(player.getUniqueId(), input);
    }

    @Subcommand("withdraw")
    @Description("Retire de l'argent de votre banque personelle")
    void withdraw(
            OMCPlayer player,
            @Named("montant") String input
    ) {
        if (player.city().hasCity() || !FeaturesRewards.hasUnlockFeature(player.city().getCity(), FeaturesRewards.Feature.PLAYER_BANK)) {
            player.message().sendError(
                    TranslationManager.translation(
                            "feature.economy.bank.command.not_unlocked",
                            Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.CITY
            );
            return;
        }

        bankManager.withdraw(player.getUniqueId(), input);
    }

    @Subcommand({"balance", "bal"})
    void withdraw(OMCPlayer player) {
        if (player.city().hasCity() || !FeaturesRewards.hasUnlockFeature(player.city().getCity(), FeaturesRewards.Feature.PLAYER_BANK)) {
            player.message().sendError(
                    TranslationManager.translation(
                            "feature.economy.bank.command.not_unlocked",
                            Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.PLAYER_BANK)).color(NamedTextColor.YELLOW)
                    ), Prefix.CITY);
        }

        double balance = bankManager.getBankBalance(player.getUniqueId());
        player.message().sendInfo(
                TranslationManager.translation(
                        "feature.economy.bank.command.balance",
                        Component.text(EconomyUtils.getFormattedSimplifiedNumber(balance)).color(NamedTextColor.LIGHT_PURPLE),
                        Component.text(economyManager.getEconomyIcon())
                ),
                Prefix.BANK
        );
    }

    @Subcommand("admin interest apply")
    @Description("Distribue les intérèts à tout les joueurs et a toute les villes")
    @CommandPermission("omc.admins.commands.bank.interest.apply")
    void applyInterest(OMCPlayer player) {
        player.message().sendInfo(TranslationManager.translation("feature.economy.bank.interest.apply.start"), Prefix.BANK);
        bankManager.applyAllPlayerInterests();
        OMCRegistry.CITY_FEATURES.CITY_BANK.applyAllCityInterests();
        player.message().sendInfo(TranslationManager.translation("feature.economy.bank.interest.apply.success"), Prefix.BANK);
    }
}

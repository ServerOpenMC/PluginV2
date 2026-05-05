package fr.openmc.core.features.city.sub.bank;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.commands.CityBankCommand;
import fr.openmc.core.features.city.sub.bank.conditions.CityBankConditions;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.milestone.rewards.InterestRewards;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.InputUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;


public class CityBankManager {

    public static void init() {
        CommandsManager.getHandler().register(
                new CityBankCommand()
        );
    }

    /**
     * Adds money to the city bank and removes it from {@link Player}
     *
     * @param player The player depositing into the bank
     * @param input  The input string to get the money value
     */
    public static void depositCityBank(City city, Player player, String input) {
        if (!CityBankConditions.canCityDeposit(city, player)) return;

        if (!InputUtils.isInputMoney(input)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.global.invalid_input"),
                    Prefix.CITY, MessageType.ERROR, true);
            return;
        }

        double amount = InputUtils.convertToMoneyValue(input);

        if (city == null || city.getLevel() < 2) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.bank.errors.min_level"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!EconomyManager.withdrawBalance(player.getUniqueId(), amount)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("messages.global.player_missing_money"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.updateBalance(amount);

        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.city.bank.deposit.success",
                        Component.text(EconomyManager.getFormattedNumber(amount))
                ),
                Prefix.CITY, MessageType.SUCCESS, false);
    }

    /**
     * Removes money from the city bank and add it to {@link Player}
     *
     * @param player The player withdrawing from the bank
     * @param input  The input string to get the money value
     */
    public static void withdrawCityBank(City city, Player player, String input) {
        if (!CityBankConditions.canCityWithdraw(city, player)) return;

        if (!InputUtils.isInputMoney(input)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.global.invalid_input"),
                    Prefix.CITY, MessageType.ERROR, true);
            return;
        }

        double amount = InputUtils.convertToMoneyValue(input);

        if (city.getBalance() < amount) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.bank.errors.not_enough_city_money"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.updateBalance(-amount);
        EconomyManager.addBalance(player.getUniqueId(), amount, "Retrait banque de ville");

        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.city.bank.withdraw.success",
                        Component.text(EconomyManager.getFormattedSimplifiedNumber(amount)).color(NamedTextColor.LIGHT_PURPLE),
                        Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.LIGHT_PURPLE)
                ),
                Prefix.CITY, MessageType.SUCCESS, false);
    }

    /**
     * Calculates the interest for the city
     * Interests calculated as proportion not percentage (e.g.: 0.01 = 1%)
     *
     * @return The calculated interest as a double.
     */
    public static double calculateCityInterest(City city) {
        double interest = .01; // base interest is 1%

        interest += InterestRewards.getTotalInterest(city.getLevel());

        if (MayorManager.phaseMayor == 2) {
            if (PerkManager.hasPerk(city.getMayor(), Perks.BUSINESS_MAN.getId())) {
                interest += .02; // interest is +2% when perk Business Man enabled
            }
        }

        return interest;
    }

    /**
     * Applies the interest to the city balance and updates it in the database.
     */
    public static void applyCityInterest(City city) {
        double interest = calculateCityInterest(city);
        double amount = city.getBalance() * interest;

        BigDecimal rounded = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);

        city.updateBalance(rounded.doubleValue());
    }

    /**
     * Apply all city interests
     * WARNING: THIS FUNCTION IS VERY EXPENSIVE DO NOT RUN FREQUENTLY IT WILL AFFECT PERFORMANCE IF THERE ARE MANY CITIES SAVED IN THE DB
     */
    public static void applyAllCityInterests() {
        List<UUID> cityUUIDs = CityManager.getAllCityUUIDs();
        for (UUID cityUUID : cityUUIDs) {
            CityManager.getCity(cityUUID).applyCityInterest();
        }
    }
}

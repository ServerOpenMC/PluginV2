package fr.openmc.core.features.city.sub.bank.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.conditions.CityBankConditions;
import fr.openmc.core.features.city.sub.bank.menu.CityBankMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Range;

public class CityBankCommand {
    @Command({"city bank", "ville bank"})
    @Description("Ouvre le menu de la banque de ville")
    void bank(Player player) {
        if (CityManager.getPlayerCity(player.getUniqueId()) == null)
            return;

        if (!CityBankConditions.canOpenCityBank(CityManager.getPlayerCity(player.getUniqueId()), player)) return;

        new CityBankMenu(player).open();
    }

    @Command({"city bank deposit", "ville bank deposit"})
    @Description("Met de votre argent dans la banque de ville")
    void deposit(Player player,
                 @Named("montant") @Range(min = 1) String input) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (!CityBankConditions.canCityDeposit(city, player)) return;

        city.depositCityBank(player, input);
    }

    @Command({"city bank withdraw", "ville bank withdraw"})
    @Description("Prend de l'argent de la banque de ville")
    void withdraw(Player player,
                  @Named("montant") @Range(min = 1) String input) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (!CityBankConditions.canCityWithdraw(city, player)) return;

        city.withdrawCityBank(player, input);
    }
}

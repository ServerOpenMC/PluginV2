package fr.openmc.core.features.city.models.city.namespaces;

import org.bukkit.entity.Player;

public interface CityEconomy {

    double getBalance();

    void setBalance(double value);

    void updateBalance(double diff);

    void depositCityBank(Player player, String input);

    void withdrawCityBank(Player player, String input);

    double calculateCityInterest();

    void applyCityInterest();
}

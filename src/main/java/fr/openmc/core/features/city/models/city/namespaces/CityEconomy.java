package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.api.omcplayer.OMCPlayer;
import org.bukkit.entity.Player;

public interface CityEconomy {

    double getBalance();

    void setBalance(double value);

    void updateBalance(double diff);

    void depositCityBank(OMCPlayer player, String input);

    void withdrawCityBank(OMCPlayer player, String input);

    double calculateCityInterest();

    void applyCityInterest();
}

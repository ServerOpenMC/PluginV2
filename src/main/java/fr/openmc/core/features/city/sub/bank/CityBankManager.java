package fr.openmc.core.features.city.sub.bank;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.commands.CityBankCommand;

import java.util.List;


public class CityBankManager {

    public CityBankManager() {

        CommandsManager.getHandler().register(
                new CityBankCommand()
        );
    }

    /**
     * Apply all city interests
     * WARNING: THIS FUNCTION IS VERY EXPENSIVE DO NOT RUN FREQUENTLY IT WILL AFFECT PERFORMANCE IF THERE ARE MANY CITIES SAVED IN THE DB
     */
    public static void applyAllCityInterests() {
        List<String> cityUUIDs = CityManager.getAllCityUUIDs();
        for (String cityUUID : cityUUIDs) {
            CityManager.getCity(cityUUID).applyCityInterest();
        }
    }
}

package fr.openmc.core.features.city.sub.bank;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.city.sub.bank.commands.CityBankCommand;


public class CityBankManager {

    public CityBankManager() {

        CommandsManager.getHandler().register(
                new CityBankCommand()
        );
    }
}

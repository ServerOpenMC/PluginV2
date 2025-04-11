package fr.openmc.core.features.economy;

import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.economy.commands.BankCommands;

public class BankManager {
    public BankManager() {
        CommandsManager.getHandler().register(new BankCommands());
    }
}

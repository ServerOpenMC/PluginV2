package fr.openmc.core.features.hdv;

import fr.openmc.core.features.hdv.commands.HDVCommand;
import fr.openmc.core.CommandsManager;

public class HDVInitializer {
    public static void init() {
        new HDVModule();
        CommandsManager.getHandler().register(new HDVCommand());
    }
}


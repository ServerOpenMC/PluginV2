package fr.openmc.core.features.tpa;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.tpa.commands.TPACommand;
import fr.openmc.core.features.tpa.commands.TPAcceptCommand;
import fr.openmc.core.features.tpa.commands.TPCancelCommand;
import fr.openmc.core.features.tpa.commands.TPDenyCommand;

public class TPAManager {
	
	public TPAManager() {
		CommandsManager.getHandler().register(
				new TPAcceptCommand(),
				new TPACommand(OMCPlugin.getInstance()),
				new TPDenyCommand(),
				new TPCancelCommand()
		);
	}
}

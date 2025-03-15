package fr.openmc.core.features.tpa;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;

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

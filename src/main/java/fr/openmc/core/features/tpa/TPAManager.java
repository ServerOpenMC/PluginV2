package fr.openmc.core.features.tpa;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.tpa.commands.TPACommand;

public class TPAManager {
	
	public TPAManager() {
		CommandsManager.getHandler().register(
				new TPACommand(OMCPlugin.getInstance())
		);
	}
}

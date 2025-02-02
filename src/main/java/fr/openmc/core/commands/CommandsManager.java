package fr.openmc.core.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.debug.CooldownCommand;
import fr.openmc.core.commands.fun.Playtime;
import fr.openmc.core.commands.fun.Diceroll;
import fr.openmc.core.commands.utils.*;
import fr.openmc.core.features.contest.commands.ContestCommand;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.utils.cooldown.CooldownInterceptor;
import lombok.Getter;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public class CommandsManager {
    @Getter static CommandsManager instance;
    @Getter static BukkitCommandHandler handler;

    public CommandsManager() {
        instance = this;
        OMCPlugin plugin = OMCPlugin.getInstance();
        handler = BukkitCommandHandler.create(plugin);

        handler.registerCondition(new CooldownInterceptor());

        registerSuggestions();
        registerCommands();
    }

    private void registerCommands() {
        handler.register(
                new Socials(),
                new Spawn(),
                new SetSpawn(),
                new Playtime(),
		        new Diceroll(),
                new CooldownCommand()
        );
    }

    private void registerSuggestions() {}
}

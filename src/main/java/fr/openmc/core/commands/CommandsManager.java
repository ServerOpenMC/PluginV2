package fr.openmc.core.commands;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public class CommandsManager {
    @Getter static CommandsManager instance;
    @Getter static BukkitCommandHandler handler;

    public CommandsManager() {
        instance = this;
        OMCPlugin plugin = OMCPlugin.getInstance();
        handler = BukkitCommandHandler.create(plugin);

        registerSuggestions();
        registerCommands();
    }

    private void registerCommands() {
        handler.register(
                new Socials(),
                new Playtime()
        );
    }

    private void registerSuggestions() {}
}

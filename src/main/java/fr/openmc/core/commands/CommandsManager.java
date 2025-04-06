package fr.openmc.core.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.debug.ChronometerCommand;
import fr.openmc.core.commands.debug.CooldownCommand;
import fr.openmc.core.commands.fun.Playtime;
import fr.openmc.core.commands.fun.Diceroll;
import fr.openmc.core.commands.utils.*;
import fr.openmc.core.features.friend.FriendCommand;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.mailboxes.MailboxCommand;
import fr.openmc.core.utils.cooldown.CooldownInterceptor;
import fr.openmc.core.utils.freeze.FreezeCommand;
import lombok.Getter;
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
                new CooldownCommand(),
                new FreezeCommand(),
                new MailboxCommand(OMCPlugin.getInstance()),
                new ChronometerCommand(),
                new FriendCommand()
        );
    }

    private void registerSuggestions() {
        FriendManager.getInstance().initCommandSuggestion();
    }
}

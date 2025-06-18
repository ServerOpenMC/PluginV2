package fr.openmc.core;

import fr.openmc.api.cooldown.CooldownInterceptor;
import fr.openmc.core.commands.admin.freeze.FreezeCommand;
import fr.openmc.core.commands.debug.ChronometerCommand;
import fr.openmc.core.commands.debug.CooldownCommand;
import fr.openmc.core.commands.fun.Diceroll;
import fr.openmc.core.commands.fun.Playtime;
import fr.openmc.core.commands.utils.*;
import fr.openmc.core.features.adminshop.AdminShopCommand;
import fr.openmc.core.features.friend.FriendCommand;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.mailboxes.MailboxCommand;
import fr.openmc.core.features.privatemessage.SocialSpyManager;
import fr.openmc.core.features.privatemessage.command.PrivateMessageCommand;
import fr.openmc.core.features.privatemessage.command.SocialSpyCommand;
import fr.openmc.core.features.quests.command.QuestCommand;
import fr.openmc.core.features.updates.UpdateCommand;
import lombok.Getter;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public class CommandsManager {
    @Getter
    static BukkitCommandHandler handler;

    public CommandsManager() {
        handler = BukkitCommandHandler.create(OMCPlugin.getInstance());

        handler.registerCondition(new CooldownInterceptor());

        registerSuggestions();
        registerCommands();
    }

    private static void registerCommands() {
        handler.register(
                new Socials(),
                new Spawn(),
                new UpdateCommand(),
                new Rtp(),
                new SetSpawn(),
                new Playtime(),
                new Diceroll(),
                new CooldownCommand(),
                new ChronometerCommand(),
                new FreezeCommand(),
                new MailboxCommand(OMCPlugin.getInstance()),
                new FriendCommand(),
                new QuestCommand(),
                new Restart(),
                new AdminShopCommand(),
                new PrivateMessageCommand(),
                new SocialSpyCommand());
    }

    private static void registerSuggestions() {
        FriendManager.initCommandSuggestion();
    }
}

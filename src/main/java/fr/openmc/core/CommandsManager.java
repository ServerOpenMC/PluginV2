package fr.openmc.core;

import fr.openmc.api.cooldown.CooldownInterceptor;
import fr.openmc.core.commands.admin.freeze.FreezeCommand;
import fr.openmc.core.commands.debug.ChronometerCommand;
import fr.openmc.core.commands.debug.CooldownCommand;
import fr.openmc.core.commands.fun.Diceroll;
import fr.openmc.core.commands.fun.Playtime;
import fr.openmc.core.commands.utils.*;
import fr.openmc.core.features.adminshop.AdminShopCommand;
import fr.openmc.core.features.credits.CreditsCommand;
import fr.openmc.core.features.cube.CubeCommands;
import fr.openmc.core.features.events.halloween.commands.HalloweenCommands;
import fr.openmc.core.features.friend.FriendCommand;
import fr.openmc.core.features.mailboxes.MailboxCommand;
import fr.openmc.core.features.mainmenu.commands.MainMenuCommand;
import fr.openmc.core.features.privatemessage.command.PrivateMessageCommand;
import fr.openmc.core.features.privatemessage.command.SocialSpyCommand;
import fr.openmc.core.features.quests.command.QuestCommand;
import fr.openmc.core.features.settings.command.SettingsCommand;
import fr.openmc.core.features.trashbin.commands.TrashCommand;
import fr.openmc.core.features.updates.UpdateCommand;
import lombok.Getter;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;

public class CommandsManager {
    @Getter
    static Lamp handler;

    public static void init() {
        handler = BukkitLamp.builder(OMCPlugin.getInstance())
                .commandCondition(new CooldownInterceptor())
                .build();

        registerCommands();
    }

    private static void registerCommands() {
        handler.register(
                new Socials(),
                new Spawn(),
                new UpdateCommand(),
                new RTPCommands(),
                new SetSpawn(),
                new Playtime(),
                new Diceroll(),
                new CooldownCommand(),
                new ChronometerCommand(),
                new FreezeCommand(),
                new MailboxCommand(),
                new FriendCommand(),
                new QuestCommand(),
                new Restart(),
                new AdminShopCommand(),
                new MainMenuCommand(),
                new PrivateMessageCommand(),
                new SocialSpyCommand(),
                new SettingsCommand(),
                new Cooldowns(),
                new CreditsCommand(),
                new CubeCommands(),
                new HalloweenCommands(),
                new TrashCommand()
        );
    }
}

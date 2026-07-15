package fr.openmc.core.registry.mobs.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Optional;

@Command({"custommobs", "cm"})
@CommandPermission("omc.admins.commands.custommobs")
public class CustomMobsCommands {
    @Subcommand("summon")
    @CommandPermission("omc.admins.commands.custommobs.summon")
    public void summon(
            Player player,
            @SuggestWith(CustomMobsAutoComplete.class) String name
    ) {
        Optional<CustomMobEntry> mob = OMCRegistry.CUSTOM_MOBS.get(name);

        if (mob.isEmpty()) {
            MessagesManager.sendMessage(player, TranslationManager.translation("command.registry.custom_mobs.summon.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        mob.get().spawn(player.getLocation());
    }
}

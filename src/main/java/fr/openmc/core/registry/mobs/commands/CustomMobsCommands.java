package fr.openmc.core.registry.mobs.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"custommobs", "cm"})
@CommandPermission("omc.admins.commands.custommobs")
public class CustomMobsCommands {
    @Subcommand("summon")
    @CommandPermission("omc.admins.commands.custommobs.summon")
    public void summon(
            Player player,
            @SuggestWith(CustomMobsAutoComplete.class) String name
    ) {
        java.util.Optional<CustomMobEntry> mob = OMCRegistry.CUSTOM_MOBS.get(name);

        if (mob.isEmpty()) {
            MessagesManager.sendMessage(player, Component.text("Ce mob n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        mob.get().spawn(player.getLocation());
    }
}

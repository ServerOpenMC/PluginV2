package fr.openmc.core.features.dimopener.commands;

import fr.openmc.core.features.dimopener.DimensionOpenerManager;
import fr.openmc.core.features.dimopener.menu.DimensionListMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"dimension", "dimensions", "dimopener"})
public class DimensionCommands {

    @CommandPlaceholder()
    public void openMenu(Player player) {
        new DimensionListMenu(player).open();
    }

    @Subcommand("bypass")
    @CommandPermission("omc.admins.commands.dimopener.bypass")
    public void bypass(Player player) {
        if (DimensionOpenerManager.hasBypass(player)) {
            DimensionOpenerManager.removeBypass(player);
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dimopener.command.unbypass"), Prefix.STAFF, MessageType.SUCCESS, false);
            return;
        }

        DimensionOpenerManager.addBypass(player);
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.dimopener.command.bypass"), Prefix.STAFF, MessageType.SUCCESS, false);
    }
}
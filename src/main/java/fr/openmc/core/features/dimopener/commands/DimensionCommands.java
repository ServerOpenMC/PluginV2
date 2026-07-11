package fr.openmc.core.features.dimopener.commands;

import fr.openmc.core.features.dimopener.DimensionOpenerManager;
import fr.openmc.core.features.dimopener.data.DimensionData;
import fr.openmc.core.features.dimopener.menu.DimensionListMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    @Subcommand("info")
    public void info(Player player, String dimensionId) {
        DimensionData dim = DimensionOpenerManager.getDimension(dimensionId);
        if (dim == null) {
            MessagesManager.sendMessage(player, Component.text("Cette dimension n'existe pas."), Prefix.DIMOPENER, MessageType.ERROR, true);
            return;
        }

        MessagesManager.sendMessage(
                player,
                Component.text(dim.getName(), NamedTextColor.YELLOW)
                        .appendNewline()
                        .append(Component.text(dim.getDescription(), NamedTextColor.GRAY))
                        .appendNewline()
                        .append(Component.text("Etat :", NamedTextColor.GRAY))
                        .append(Component.text(DimensionOpenerManager.getProgress(dimensionId).getState().ordinal(), NamedTextColor.WHITE)),
                Prefix.DIMOPENER
        );
    }

    @Subcommand("reload")
    @CommandPermission("openmc.dimension.admin")
    public void reload(Player player) {
        DimensionOpenerManager.loadDimensions();
        MessagesManager.sendMessage(player, Component.text("Dimensions rechargées."), Prefix.DIMOPENER, MessageType.SUCCESS, true);
    }
}
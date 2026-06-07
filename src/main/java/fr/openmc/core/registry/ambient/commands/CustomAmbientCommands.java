package fr.openmc.core.registry.ambient.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.commands.autocomplete.CustomAmbientAutoComplete;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"ambient", "customambient"})
@CommandPermission("omc.admins.commands.customambient")
public class CustomAmbientCommands {
    @Subcommand("apply")
    public void applyAmbient(
            Player player,
            Player toPlayer,
            @SuggestWith(CustomAmbientAutoComplete.class) String id
    ) {
        CustomAmbient ambient = OMCRegistry.CUSTOM_AMBIENTS.get(id);

        if (ambient == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("command.registry.custom_ambient.apply.null"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        ambient.apply(toPlayer);
        MessagesManager.sendMessage(player, TranslationManager.translation("command.registry.custom_ambient.apply.success",
                Component.text(id).color(NamedTextColor.YELLOW),
                Component.text(toPlayer.getName()).color(NamedTextColor.YELLOW)
                ), Prefix.STAFF, MessageType.ERROR, true);
    }

    @Subcommand("reset")
    public void resetAmbient(
            Player player,
            Player toPlayer
    ) {
        if (!CustomAmbient.ACTIVE_AMBIENTS.containsKey(toPlayer.getUniqueId())) {
            MessagesManager.sendMessage(player, TranslationManager.translation("command.registry.custom_ambient.reset.player_havnt_ambient"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        String idAmbient = CustomAmbient.ACTIVE_AMBIENTS.get(toPlayer.getUniqueId());

        OMCRegistry.CUSTOM_AMBIENTS.get(idAmbient).reset(toPlayer);
        MessagesManager.sendMessage(player, TranslationManager.translation("command.registry.custom_ambient.reset.success",
                Component.text(toPlayer.getName()).color(NamedTextColor.YELLOW)
        ), Prefix.STAFF, MessageType.ERROR, true);
    }
}

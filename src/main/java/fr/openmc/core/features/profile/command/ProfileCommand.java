package fr.openmc.core.features.profile.command;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.profile.menu.ProfileMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"profile", "profil"})
@CommandPermission("omc.commands.profile")
@Description("Ouvre le profil d'un joueur")
public class ProfileCommand {
    @CommandPlaceholder
    public void openProfile(
            Player player,
            @Named("joueur") @Optional @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target
    ) {
        OfflinePlayer selectedTarget = target == null ? player : target;
        if (!selectedTarget.isOnline() && !selectedTarget.hasPlayedBefore()) {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation("feature.profile.message.player_not_found"),
                    Prefix.OPENMC,
                    MessageType.ERROR,
                    true
            );
            return;
        }

        new ProfileMenu(player, selectedTarget).open();
    }
}

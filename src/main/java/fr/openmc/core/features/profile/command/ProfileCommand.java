package fr.openmc.core.features.profile.command;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.profile.menu.ProfileMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"profile", "profil"})
@CommandPermission("omc.commands.profile")
@Description("Ouvre le profil d'un joueur")
public class ProfileCommand {
    @CommandPlaceholder
    public void openProfile(
            OMCPlayer player,
            @Named("joueur") @Optional @SuggestWith(OnlinePlayerAutoComplete.class) OMCOfflinePlayer target
    ) {
        if (!target.isConnected() && !target.hasPlayedBefore()) {
            player.message().send(
                    TranslationManager.translation("feature.profile.message.player_not_found"),
                    Prefix.OPENMC,
                    MessageType.ERROR,
                    true
            );
            return;
        }

        new ProfileMenu(player, target).open();
    }
}

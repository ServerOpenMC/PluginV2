package fr.openmc.core.features.privatemessage.command;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.privatemessage.SocialSpyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("socialspy")
public class SocialSpyCommand {

    @CommandPlaceholder()
    @Description("Active ou désactive le social spy")
    @CommandPermission("omc.admin.commands.privatemessage.socialspy")
    public void toggleSocialSpyCommand(Player player) {
        SocialSpyManager.toggleSocialSpy(player);
    }

    @Subcommand("toggle")
    @Description("Active ou désactive le social spy pour un joueur spécifique")
    @CommandPermission("omc.admin.commands.privatemessage.socialspy.admin")
    public void toggleSocialSpyForPlayer(
            Player admin,
            @SuggestWith(OnlinePlayerAutoComplete.class) @Named("target") Player target
    ) {
        SocialSpyManager.toggleSocialSpy(target);

        Component status = SocialSpyManager.hasSocialSpyEnabled(target)
                ? TranslationManager.translation("feature.privatemessage.socialspy.status.enabled")
                : TranslationManager.translation("feature.privatemessage.socialspy.status.disabled");

        MessagesManager.sendMessage(admin,
                TranslationManager.translation("feature.privatemessage.socialspy.status_changed",
                        status,
                        Component.text(target.getName())),
                Prefix.OPENMC, MessageType.SUCCESS, true);
    }

    @Subcommand("list")
    @Description("Liste les joueurs ayant le social spy activé")
    @CommandPermission("omc.admin.commands.privatemessage.socialspy.admin")
    public void listSocialSpyPlayers(Player admin) {
        int spyCount = 0;
        Component spyList = Component.empty();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (SocialSpyManager.hasSocialSpyEnabled(onlinePlayer)) {
                if (spyCount > 0) {
                    spyList = spyList.appendNewline();
                }
                spyList = spyList.append(TranslationManager.translation(
                        "feature.privatemessage.socialspy.list_item",
                        Component.text(onlinePlayer.getName())
                ));
                spyCount++;
            }
        }

        if (spyCount == 0) {
            MessagesManager.sendMessage(admin,
                    TranslationManager.translation("feature.privatemessage.socialspy.no_players"),
                    Prefix.OPENMC, MessageType.INFO, true);
        } else {
            admin.sendMessage(TranslationManager.translation("feature.privatemessage.socialspy.list_title"));
            admin.sendMessage(spyList);
            admin.sendMessage(TranslationManager.translation("feature.privatemessage.socialspy.list_total", Component.text(spyCount)));
        }
    }
}

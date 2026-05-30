package fr.openmc.core.features.homes.command;

import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.command.autocomplete.HomeAutoComplete;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class RenameHomeCommand {

    @Command("renamehome")
    @Description("Renomme votre home")
    @CommandPermission("omc.commands.home.rename")
    public void renameHome(
            Player player,
            @Named("home") @SuggestWith(HomeAutoComplete.class) String home,
            @Named("nouveau nom de home") String newName
    ) {
        if(player.hasPermission("omc.admin.homes.rename.other") && home.contains(":")) {
            String[] split = home.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if(!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            if (!HomeUtil.isValidHomeName(newName)) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.invalid_name"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());
            for (Home h : homes) {
                if (!h.getName().equalsIgnoreCase(homeName)) {
                    continue;
                }
                if(h.getName().equalsIgnoreCase(newName)) {
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.other_already_has"), Prefix.HOME, MessageType.ERROR, true);
                    return;
                }

                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.homes.command.rename.other.success",
                                Component.text(h.getName()).color(NamedTextColor.YELLOW),
                                Component.text(newName).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.HOME,
                        MessageType.SUCCESS,
                        true
                );
                HomesManager.renameHome(h, newName);
                return;
            }

            MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.other_no_home_with_name"), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        if (!HomeUtil.isValidHomeName(newName)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.invalid_name"), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        for (Home h : homes) {
            if(!h.getName().equalsIgnoreCase(home)) {
                continue;
            }
            if(h.getName().equalsIgnoreCase(newName)) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.rename.already_has"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.homes.command.rename.self.success",
                            Component.text(h.getName()).color(NamedTextColor.YELLOW),
                            Component.text(newName).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.HOME,
                    MessageType.SUCCESS,
                    true
            );
            HomesManager.renameHome(h, newName);
            return;
        }

        MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.self_no_home_with_name"), Prefix.HOME, MessageType.ERROR, true);
    }
}

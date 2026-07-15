package fr.openmc.core.features.homes.command;

import fr.openmc.api.entity.player.OMCOfflinePlayer;
import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.features.homes.command.autocomplete.HomeAutoComplete;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
            OMCPlayer player,
            @Named("home") @SuggestWith(HomeAutoComplete.class) String home,
            @Named("nouveau nom de home") String newName
    ) {
        if (player.hasPermission("omc.admin.homes.rename.other") && home.contains(":")) {
            String[] split = home.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OMCOfflinePlayer target = OMCOfflinePlayer.of(Bukkit.getOfflinePlayer(targetName));

            if (!target.hasPlayedBefore()) {
                player.message().sendError(TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.HOME, true);
                return;
            }

            if (!HomeUtil.isValidHomeName(newName)) {
                player.message().sendError(TranslationManager.translation("feature.homes.command.invalid_name"), Prefix.HOME, true);
                return;
            }

            List<Home> homes = target.home().getHomes();
            for (Home h : homes) {
                if (!h.getName().equalsIgnoreCase(homeName)) {
                    continue;
                }
                if (h.getName().equalsIgnoreCase(newName)) {
                    player.message().sendError(TranslationManager.translation("feature.homes.command.other_already_has"), Prefix.HOME, true);
                    return;
                }

                player.message().sendSuccess(
                        TranslationManager.translation(
                                "feature.homes.command.rename.other.success",
                                Component.text(h.getName()).color(NamedTextColor.YELLOW),
                                Component.text(newName).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.HOME,
                        true
                );
                player.home().renameHome(h, newName);
                return;
            }

            player.message().sendError(TranslationManager.translation("feature.homes.command.other_no_home_with_name"), Prefix.HOME, true);
            return;
        }

        if (!HomeUtil.isValidHomeName(newName)) {
            player.message().sendError(TranslationManager.translation("feature.homes.command.invalid_name"), Prefix.HOME, true);
            return;
        }

        List<Home> homes = player.home().getHomes();

        for (Home h : homes) {
            if (!h.getName().equalsIgnoreCase(home)) {
                continue;
            }
            if (h.getName().equalsIgnoreCase(newName)) {
                player.message().sendError(TranslationManager.translation("feature.homes.command.rename.already_has"), Prefix.HOME, true);
                return;
            }
            player.message().sendSuccess(
                    TranslationManager.translation(
                            "feature.homes.command.rename.self.success",
                            Component.text(h.getName()).color(NamedTextColor.YELLOW),
                            Component.text(newName).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.HOME,
                    true
            );
            player.home().renameHome(h, newName);
            return;
        }

        player.message().sendError(TranslationManager.translation("feature.homes.command.self_no_home_with_name"), Prefix.HOME, true);
    }
}

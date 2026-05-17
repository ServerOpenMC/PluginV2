package fr.openmc.core.features.homes.command;

import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.command.autocomplete.HomeAutoComplete;
import fr.openmc.core.features.homes.models.Home;
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

public class DelHomeCommand {

    @Command("delhome")
    @Description("Supprime un home")
    @CommandPermission("omc.commands.home.delhome")
    public void delHome(
            Player player,
            @Named("home") @SuggestWith(HomeAutoComplete.class) String name
    ) {
        if(player.hasPermission("omc.admin.homes.delhome.other") && name.contains(":")) {
            String[] split = name.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if(!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());
            for(Home home : homes) {
                if(home.getName().equalsIgnoreCase(homeName)) {
                    HomesManager.removeHome(home);
                    MessagesManager.sendMessage(
                            player,
                            TranslationManager.translation(
                                    "feature.homes.command.delete.other.success",
                                    Component.text(home.getName()).color(NamedTextColor.YELLOW)
                            ),
                            Prefix.HOME,
                            MessageType.SUCCESS,
                            true
                    );
                    return;
                }
            }

            MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.other_no_home_with_name"), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        for(Home home : homes) {
            if(home.getName().equalsIgnoreCase(name)) {
                HomesManager.removeHome(home);
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.homes.command.delete.self.success",
                                Component.text(home.getName()).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.HOME,
                        MessageType.SUCCESS,
                        true
                );
                return;
            }
        }

        MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.self_no_home_with_name"), Prefix.HOME, MessageType.ERROR, true);
    }
}

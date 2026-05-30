package fr.openmc.core.features.homes.command;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.command.autocomplete.HomeAutoComplete;
import fr.openmc.core.features.homes.events.HomeCreateEvent;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
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
import java.util.UUID;

public class SetHomeCommand {

    @Command("sethome")
    @Description("Permet de définir votre home")
    @CommandPermission("omc.commands.home.sethome")
    public void setHome(
            Player player,
            @Named("home") @SuggestWith(HomeAutoComplete.class) String name
    ) {
        if(DisabledWorldHome.isDisabledWorld(player.getWorld())) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.disabled_world"), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        if(player.hasPermission("omc.admin.homes.sethome.other") && name.contains(":")) {
            String[] split = name.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if(!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            if(split.length < 2) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            if (!HomeUtil.isValidHomeName(homeName)) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.invalid_name"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }
            List<Home> homes = HomesManager.getHomes(target.getUniqueId());
            for (Home home : homes) {
                if (home.getName().equalsIgnoreCase(homeName)) {
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.other_already_has"), Prefix.HOME, MessageType.ERROR, true);
                    return;
                }
            }

            Home home = new Home(UUID.randomUUID(), target.getUniqueId(), homeName, player.getLocation(), HomeIconRegistry.getDefaultIcon());
            HomesManager.addHome(home);

            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.homes.command.set.other.success",
                            Component.text(homeName).color(NamedTextColor.YELLOW),
                            Component.text(targetName).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.HOME,
                    MessageType.SUCCESS,
                    true
            );
            if(target.isOnline() && target instanceof Player targetPlayer) {
                MessagesManager.sendMessage(
                        targetPlayer,
                        TranslationManager.translation(
                                "feature.homes.command.set.by_admin",
                                Component.text(homeName).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.HOME,
                        MessageType.SUCCESS,
                        true
                );
            }

            return;
        }

        if (!HomeUtil.isValidHomeName(name)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.invalid_name"), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        int currentHome = HomesManager.getHomes(player.getUniqueId()).size();
        int homesLimit = HomesManager.getHomeLimit(player.getUniqueId());

        if(currentHome >= homesLimit) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.home_limit_reached"), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.already_has"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }
        }

        Home home = new Home(UUID.randomUUID(), player.getUniqueId(), name, player.getLocation(), HomeIconRegistry.getDefaultIcon());
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new HomeCreateEvent(home, player));
        });
        HomesManager.addHome(home);


        MessagesManager.sendMessage(
                player,
                TranslationManager.translation(
                        "feature.homes.command.set.self.success",
                        Component.text(name).color(NamedTextColor.YELLOW)
                ),
                Prefix.HOME,
                MessageType.SUCCESS,
                true
        );
    }
}

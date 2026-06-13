package fr.openmc.core.features.homes.command;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.command.autocomplete.HomeAutoComplete;
import fr.openmc.core.features.homes.events.HomeTpEvent;
import fr.openmc.core.features.homes.menu.HomeMenu;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class TpHomeCommand {

    @Command("home")
    @Description("Se téléporter à un home")
    @CommandPermission("omc.commands.home.teleport")
    public static void home(
            Player player,
            @Named("home") @Optional @SuggestWith(HomeAutoComplete.class) String home
    ) {

        if(home != null && home.contains(":") && player.hasPermission("omc.admin.homes.teleport.others")) {
            String[] split = home.split(":");
            String targetName = split[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if(!player.isConnected() && !target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());


            if(split.length < 2) {
                if(homes.isEmpty()) {
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.other_no_home"), Prefix.HOME, MessageType.ERROR, true);
                    return;
                }

                Menu menu = new HomeMenu(player, target);
                menu.open();
                return;
            }

            for(Home h : homes) {
                if (h.getName().equalsIgnoreCase(split[1])) {
                    PlayerUtils.sendFadeTitleTeleport(player, h.getLocation());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getPluginManager().callEvent(new HomeTpEvent(h, player));
                        }
                    }.runTask(OMCPlugin.getInstance());
                    MessagesManager.sendMessage(
                            player,
                            TranslationManager.translation(
                                    "feature.homes.command.teleport.other.success",
                                    Component.text(h.getName()).color(NamedTextColor.YELLOW),
                                    Component.text(target.getName()).color(NamedTextColor.YELLOW)
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

        if(home == null || home.isBlank() || home.isEmpty()) {
            if(homes.isEmpty()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.no_home"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            Menu menu = new HomeMenu(player);
            menu.open();
            return;
        }

        for(Home h : homes) {
            if(h.getName().equalsIgnoreCase(home)) {
                PlayerUtils.sendFadeTitleTeleport(player, h.getLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(new HomeTpEvent(h, player));
                    }
                }.runTask(OMCPlugin.getInstance());
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.homes.command.teleport.self.success",
                                Component.text(h.getName()).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.HOME,
                        MessageType.SUCCESS,
                        true
                );
                return;
            }
        }

        MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.no_home_with_name"), Prefix.HOME, MessageType.ERROR, true);
    }

}

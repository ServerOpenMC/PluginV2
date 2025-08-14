package fr.openmc.core.features.homes.command;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.menu.HomeMenu;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class TpHome {
    @Command("home")
    @Description("Se téléporter à un home")
    @CommandPermission("omc.commands.home.teleport")
    @AutoComplete("@homes")
    public static void home(Player player, @Optional String home) {

        if(home != null && home.contains(":") && player.hasPermission("omc.admin.homes.teleport.others")) {
            String[] split = home.split(":");
            String targetName = split[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if(!player.isConnected() && !target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, Component.text("§cCe joueur n'existe pas."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());


            if(split.length < 2) {
                if(homes.isEmpty()) {
                    MessagesManager.sendMessage(player, Component.text("§cCe joueur n'a pas de home."), Prefix.HOME, MessageType.ERROR, true);
                    return;
                }

                Menu menu = new HomeMenu(player, target);
                menu.open();
                return;
            }

            for(Home h : homes) {
                if (h.getName().equalsIgnoreCase(split[1])) {
                    PlayerUtils.sendFadeTitleTeleport(player, h.getLocation());
                    MessagesManager.sendMessage(player, Component.text("§aVous avez été téléporté au home §e" + h.getName() + " §ade §e" + target.getName() + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
                    return;
                }
            }

            MessagesManager.sendMessage(player, Component.text("§cCe joueur n'a pas de home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        if(home == null || home.isBlank() || home.isEmpty()) {
            if(homes.isEmpty()) {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de home."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            Menu menu = new HomeMenu(player);
            menu.open();
            return;
        }

        for(Home h : homes) {
            if(h.getName().equalsIgnoreCase(home)) {
                PlayerUtils.sendFadeTitleTeleport(player, h.getLocation());
                MessagesManager.sendMessage(player, Component.text("§aVous avez été téléporté à votre home §e" + h.getName() + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
                return;
            }
        }

        MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
    }

}

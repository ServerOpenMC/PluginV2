package fr.openmc.core.features.homes.command;

import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.HomesManager;
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
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class DelHome {

    private final HomesManager homesManager;
    MessagesManager msg;
    public DelHome(HomesManager homesManager) {
        this.homesManager = homesManager;
    }

    @Command("delhome")
    @Description("Supprime un home")
    @CommandPermission("omc.commands.home.delhome")
    @AutoComplete("@homes")
    public void delHome(Player player, String name) {
        if(player.hasPermission("omc.admin.homes.delhome.other") && name.contains(":")) {
            String[] split = name.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if(!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, Component.text("§cCe joueur n'existe pas."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());
            for(Home home : homes) {
                if(home.getName().equalsIgnoreCase(homeName)) {
                    homesManager.removeHome(home);
                    MessagesManager.sendMessage(player, Component.text("§aLe home §e" + home.getName() + " §aa été supprimé."), Prefix.HOME, MessageType.SUCCESS, true);
                    return;
                }
            }

            MessagesManager.sendMessage(player, Component.text("§cCe joueur n'a pas de home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        for(Home home : homes) {
            if(home.getName().equalsIgnoreCase(name)) {
                homesManager.removeHome(home);
                MessagesManager.sendMessage(player, Component.text("§aTon home §e" + home.getName() + " §aa été supprimé."), Prefix.HOME, MessageType.SUCCESS, true);
                return;
            }
        }

        MessagesManager.sendMessage(player, Component.text("§cTu n'as pas de home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
    }
}

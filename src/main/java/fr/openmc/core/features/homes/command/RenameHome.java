package fr.openmc.core.features.homes.command;

import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.utils.HomeUtil;
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

public class RenameHome {

    private MessagesManager msg;
    private final HomesManager homesManager;
    public RenameHome(HomesManager homesManager) {
        this.homesManager = homesManager;
    }

    @Command("renamehome")
    @Description("Renomme votre home")
    @CommandPermission("omc.commands.home.rename")
    @AutoComplete("@homes")
    public void renameHome(Player player, String home, String newName) {

        if(player.hasPermission("omc.admin.homes.rename.other") && home.contains(":")) {
            String[] split = home.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if(!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, Component.text("§cCe joueur n'existe pas."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            if (HomeUtil.checkName(player, msg, newName)) return;

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());
            for (Home h : homes) {
                if (!h.getName().equalsIgnoreCase(homeName)) {
                    continue;
                }
                if(h.getName().equalsIgnoreCase(newName)) {
                    MessagesManager.sendMessage(player, Component.text("§cCe joueur a déjà un home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
                    return;
                }

                MessagesManager.sendMessage(player, Component.text("§aLe home §e" + h.getName() + " §aa été renommé en §e" + newName + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
                homesManager.renameHome(h, newName);
                return;
            }

            MessagesManager.sendMessage(player, Component.text("§cCe joueur n'a pas de home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        if(HomeUtil.checkName(player, msg, newName)) return;

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        for (Home h : homes) {
            if(!h.getName().equalsIgnoreCase(home)) {
                continue;
            }
            if(h.getName().equalsIgnoreCase(newName)) {
                MessagesManager.sendMessage(player, Component.text("§cTu as déjà un home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }
            MessagesManager.sendMessage(player, Component.text("§aTon home §e" + h.getName() + " §aa été renommé en §e" + newName + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
            homesManager.renameHome(h, newName);
            return;
        }

        MessagesManager.sendMessage(player, Component.text("§cTu n'as pas de home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
    }
}

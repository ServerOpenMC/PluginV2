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

public class SetHome {

    private MessagesManager msg;
    private final HomesManager homesManager;

    public SetHome(HomesManager homesManager) {
        this.homesManager = homesManager;
    }

    @Command("sethome")
    @Description("Permet de définir votre home")
    @CommandPermission("omc.commands.home.sethome")
    @AutoComplete("@homes")
    public void setHome(Player player, String name) {

        if(homesManager.disabledWorldHome.isDisabledWorld(player.getWorld())) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas définir de home dans ce monde."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        if(player.hasPermission("omc.admin.homes.sethome.other") && name.contains(":")) {
            String[] split = name.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if(!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, Component.text("§cCe joueur n'existe pas."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            if(split.length < 2) {
                MessagesManager.sendMessage(player, Component.text("§cCe joueur n'existe pas."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            if(HomeUtil.checkName(player, msg, homeName)) return;

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());
            for (Home home : homes) {
                if (home.getName().equalsIgnoreCase(homeName)) {
                    MessagesManager.sendMessage(player, Component.text("§cCe joueur a déjà un home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
                    return;
                }
            }

            Home home = new Home(target.getUniqueId(), homeName, player.getLocation(), HomeUtil.getDefaultHomeIcon(homeName));
            homesManager.addHome(home);

            MessagesManager.sendMessage(player, Component.text("§aLe home §e" + homeName + " §aa été défini pour §e" + targetName + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
            if(target.isOnline() && target instanceof Player targetPlayer) {
                MessagesManager.sendMessage(targetPlayer, Component.text("§aUn admin vous a défini un home §e" + homeName + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
            }

            return;
        }

        if(HomeUtil.checkName(player, msg, name)) return;

        int currentHome = HomesManager.getHomes(player.getUniqueId()).size();
        int homesLimit = homesManager.getHomeLimit(player.getUniqueId());

        if(currentHome >= homesLimit) {
            MessagesManager.sendMessage(player, Component.text("§cVous avez atteint la limite de homes."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                MessagesManager.sendMessage(player, Component.text("§cVous avez déjà un home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }
        }

        Home home = new Home(player.getUniqueId(), name, player.getLocation(), HomeUtil.getDefaultHomeIcon(name));
        homesManager.addHome(home);

        MessagesManager.sendMessage(player, Component.text("§aVotre home §e" + name + " §aa été défini."), Prefix.HOME, MessageType.SUCCESS, true);
    }
}

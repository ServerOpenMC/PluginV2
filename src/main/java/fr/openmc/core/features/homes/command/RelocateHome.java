package fr.openmc.core.features.homes.command;

import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.utils.WorldGuardApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class RelocateHome {

    private MessagesManager msg;
    private final HomesManager homeManager;

    public RelocateHome(HomesManager homeManager) {
        this.homeManager = homeManager;
    }


    @Command("relocatehome")
    @Description("Déplace votre home")
    @CommandPermission("omc.commands.home.relocate")
    @AutoComplete("@homes")
    public void relocateHome(Player player, String home) {

        Location location = player.getLocation();

        if(homeManager.disabledWorldHome.isDisabledWorld(location.getWorld())) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas définir de home dans ce monde."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        if(player.hasPermission("omc.admin.homes.relocate.other") && home.contains(":")) {
            String[] split = home.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if(!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, Component.text("§cCe joueur n'existe pas."), Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }

            if (HomeUtil.checkName(player, msg, homeName)) return;

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());
            for (Home h : homes) {
                if (!h.getName().equalsIgnoreCase(homeName)) {
                    continue;
                }

                homeManager.relocateHome(h, location);
                MessagesManager.sendMessage(player, Component.text("§aLe home §e" + h.getName() + " §aa été déplacé."), Prefix.HOME, MessageType.SUCCESS, true);
                return;
            }

            MessagesManager.sendMessage(player, Component.text("§cCe joueur n'a pas de home avec ce nom."), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        if(WorldGuardApi.isRegionConflict(location)) {
            MessagesManager.sendMessage(player, Component.text("§cTu ne peux pas définir un home ici, tu es dans une région protégée."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        for(Home h : homes) {
            if(h.getName().equalsIgnoreCase(home)) {
                homeManager.relocateHome(h, location);
                MessagesManager.sendMessage(player, Component.text("§aTon home §e" + h.getName() + " §aa été déplacé."), Prefix.HOME, MessageType.SUCCESS, true);
                return;
            }
        }

        MessagesManager.sendMessage(player, Component.text("§cTu n'as pas de home avec ce nom."), Prefix.OPENMC, MessageType.ERROR, true);
    }

}

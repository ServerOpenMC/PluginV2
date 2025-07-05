package fr.openmc.core.features.homes.command;

import fr.openmc.core.features.homes.menu.HomeUpgradeMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class UpgradeHome {
    @Command({"upgradehome", "uphome"})
    @Description("Améliore le nombre de homes que tu peux avoir")
    @CommandPermission("omc.commands.home.upgradehome")
    public void upgradeHome(Player player) {
        new HomeUpgradeMenu(player).open();
    }
}

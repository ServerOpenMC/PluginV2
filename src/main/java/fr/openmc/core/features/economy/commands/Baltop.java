package fr.openmc.core.features.economy.commands;

import fr.openmc.core.features.leaderboards.LeaderBoardManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Baltop {

    @Command("baltop")
    @Description("Permet de voir le top des joueurs les plus riches")
    @CommandPermission("omc.commands.baltop")
    public void baltop(Player player) {
         player.sendMessage(LeaderBoardManager.cityMoneyLeaderBoard.createComponent());
    }
}

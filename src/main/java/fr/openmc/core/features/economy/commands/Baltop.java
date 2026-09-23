package fr.openmc.core.features.economy.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.leaderboards.LeaderBoardManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Baltop {
    private final LeaderBoardManager leaderBoardManager = OMCRegistry.FEATURES.LEADERBOARD.get();
    @Command("baltop")
    @Description("Permet de voir le top des joueurs les plus riches")
    @CommandPermission("omc.commands.baltop")
    public void baltop(Player player) {
         player.sendMessage(leaderBoardManager.CITY_MONEY_LEADERBOARD.createComponent());
    }
}

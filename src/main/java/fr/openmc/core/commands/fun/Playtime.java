package fr.openmc.core.commands.fun;

import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Playtime {
    private final MessagesManager msgOMC  = new MessagesManager(Prefix.OPENMC);

    @Command("playtime")
    @CommandPermission("omc.commands.playtime")
    @Description("Donne votre temps de jeu")
    private void playtime(Player player) {
        long timePlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        msgOMC.info(player, "Vous avez §d" + DateUtils.convertTime(timePlayed) + " §7de temps de jeu.");
    }
}

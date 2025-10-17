package fr.openmc.core.features.displays.scoreboards;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

import static fr.openmc.core.features.displays.scoreboards.ScoreboardManager.*;

@Command("sb")
@CommandPermission("omc.commands.scoreboard")
@Description("Active / désactive le scoreboard")
public class ScoreboardCommand {

    @CommandPlaceholder()
    public void onScoreboardCommand(Player player) {
        UUID uuid = player.getUniqueId();
        if (disabledPlayers.contains(uuid)) {
            disabledPlayers.remove(uuid);

            playerScoreboards.remove(uuid);
            player.setScoreboard(createNewScoreboard(player));
            updateScoreboard(player);

            MessagesManager.sendMessage(player, Component.text("Scoreboard activé").color(NamedTextColor.GREEN), Prefix.OPENMC, MessageType.INFO, true);
        } else {
            disabledPlayers.add(uuid);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            MessagesManager.sendMessage(player, Component.text("Scoreboard désactivé").color(NamedTextColor.RED), Prefix.OPENMC, MessageType.INFO, true);
        }
    }
}

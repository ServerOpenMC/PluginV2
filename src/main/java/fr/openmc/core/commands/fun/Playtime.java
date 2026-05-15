package fr.openmc.core.commands.fun;

import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Playtime {
    @Command("playtime")
    @CommandPermission("omc.commands.playtime")
    @Description("Donne votre temps de jeu")
    private void playtime(Player player) {
        long timePlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        MessagesManager.sendMessage(player, TranslationManager.translation("command.fun.playtime.success",
                Component.text(DateUtils.convertTime(timePlayed), NamedTextColor.LIGHT_PURPLE)), Prefix.OPENMC, MessageType.INFO, true);
    }
}

package fr.openmc.core.commands.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Restart {

    public static boolean isRestarting = false;
    public static int remainingTime = -1;
    private static List<Integer> annouce = List.of(60, 30, 15, 10, 5, 4, 3, 2, 1);

    @Command("omcrestart")
    @Description("Redémarre le serveur après 1min")
    @CommandPermission("omc.admin.commands.restart")
    public void restart(CommandSender sender) {
        if (sender instanceof Player) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, false);
            return;
        }

        isRestarting = true;
        remainingTime = 60;

        OMCPlugin plugin = OMCPlugin.getInstance();
        BukkitRunnable update = new BukkitRunnable() {;
            @Override
            public void run() {
                if (remainingTime == 0)
                    Bukkit.getServer().restart();

                if (!annouce.contains(remainingTime)) {
                    remainingTime -= 1;
                    return;
                }

                Component broadcast = Component.text("§7(" + MessageType.WARNING.getPrefix() + "§7) ")
                        .append(MiniMessage.miniMessage().deserialize(Prefix.OPENMC.getPrefix()))
                        .append(Component.text(" §7» ")
                        .append(Component.text("Redémarrage du serveur dans §d" + remainingTime + " §fseconde(s)" + (remainingTime == 1 ? "" : "s"))));

                Bukkit.broadcast(broadcast);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Title title = Title.title(Component.text("Redémarrage"), Component.text("§d" + remainingTime + " §fseconde(s)" + (remainingTime == 1 ? "" : "s")));
                    player.showTitle(title);

                    player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 0.4F);
                }
                remainingTime -= 1;
            }
        };

        update.runTaskTimer(plugin, 20, 20);
    }
}

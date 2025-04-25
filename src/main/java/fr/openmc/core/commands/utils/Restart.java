package fr.openmc.core.commands.utils;

import java.util.List;

import org.bukkit.Bukkit;
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

    private static List<Integer> annouce = List.of(60, 30, 15, 10, 5, 4, 3, 2, 1);

    @Command("omcrestart")
    @Description("Redémarre le serveur après 1min")
    @CommandPermission("omc.admin.commands.restart")
    public void restart() {
        OMCPlugin plugin = OMCPlugin.getInstance();
        BukkitRunnable update = new BukkitRunnable() {
            public int remainingTime = 60;

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
                        .append(Component.text("Redémarrage du serveur dans " + remainingTime + " seconde" + (remainingTime == 1 ? "" : "s"))));

                Bukkit.broadcast(broadcast);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Title title = Title.title(Component.text("Redémarrage"), Component.text(remainingTime + " seconde" + (remainingTime == 1 ? "" : "s")));
                    player.showTitle(title);
                }
                remainingTime -= 1;
            }
        };

        update.runTaskTimer(plugin, 20, 20);
    }
}

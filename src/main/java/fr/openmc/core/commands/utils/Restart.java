package fr.openmc.core.commands.utils;

import java.util.List;
import java.util.UUID;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Restart {

    public static boolean isRestarting = false;
    public static int remainingTime = -1;
    private static final List<Integer> announce = List.of(60, 30, 15, 10, 5, 4, 3, 2, 1);

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

        // protection pour le bug de duplication
        for (City city : CityManager.getCities()) {
            UUID watcherUUID = city.getChestWatcher();
            if (watcherUUID == null) continue;

            MessagesManager.sendMessage(sender, Component.text("§7Le coffre est inaccessible durant un rédémarrage programmé"), Prefix.OPENMC, MessageType.INFO, false);
            Bukkit.getPlayer(watcherUUID).closeInventory();
        }

        OMCPlugin plugin = OMCPlugin.getInstance();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Component kickMessage = Component.text()
                                .append(Component.text("🔄 Redémarrage du serveur 🔄\n", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                                .append(Component.text("\n"))
                                .append(Component.text("Le serveur est en train de redémarrer.\n", NamedTextColor.WHITE))
                                .append(Component.text("Merci de votre patience !", NamedTextColor.GRAY))
                                .build();
                        player.kick(kickMessage, PlayerKickEvent.Cause.RESTART_COMMAND);
                    }
                    Bukkit.getServer().restart();
                }

                if (!announce.contains(remainingTime)) {
                    remainingTime -= 1;
                    return;
                }

                MessagesManager.broadcastMessage(
                        Component.text("Redémarrage du serveur dans §d" + remainingTime + " §fseconde" + (remainingTime == 1 ? "" : "s")),
                        Prefix.OPENMC, MessageType.WARNING);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Title title = Title.title(Component.text("Redémarrage"), Component.text("§d" + remainingTime + " §fseconde" + (remainingTime == 1 ? "" : "s")));
                    player.showTitle(title);

                    player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 0.4F);
                }
                remainingTime -= 1;
            }
        }.runTaskTimer(plugin, 20, 20);
    }
}

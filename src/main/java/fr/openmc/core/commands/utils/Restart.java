package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.UUID;

public class Restart {

    public static boolean isRestarting = false;
    public static int remainingTime = -1;
    private static final List<Integer> announce = List.of(60, 30, 15, 10, 5, 4, 3, 2, 1);

    @Command("omcrestart")
    @Description("Redémarre le serveur après 1min")
    @CommandPermission("omc.admin.commands.restart")
    public void restart(CommandSender sender) {
        if (sender instanceof Player) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.global.cannot_do_this"), Prefix.OPENMC, MessageType.ERROR, false);
            return;
        }

        isRestarting = true;
        remainingTime = 60;

        // protection pour le bug de duplication
        for (City city : CityManager.getCities()) {
            UUID watcherUUID = city.getChestWatcher();
            if (watcherUUID == null) continue;
            Player player = Bukkit.getPlayer(watcherUUID);
            if (player == null || !player.isOnline()) continue;


	        MessagesManager.sendMessage(sender, TranslationManager.translation("command.utils.restart.cannot_open_city_chest"), Prefix.OPENMC, MessageType.INFO, false);
            player.closeInventory();
        }

        OMCPlugin plugin = OMCPlugin.getInstance();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Component kickMessage = Component.text()
                                .append(TranslationManager.translation("command.utils.restart.redem").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                                .append(Component.text("\n"))
                                .append(TranslationManager.translation("command.utils.restart.server_restarting").color(NamedTextColor.WHITE))
                                .append(TranslationManager.translation("command.utils.restart.thanks").color(NamedTextColor.GRAY))
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
                        TranslationManager.translation("command.utils.restart.restarting_in",
                                Component.text(remainingTime).color(NamedTextColor.LIGHT_PURPLE),
                                Component.text(remainingTime == 1 ? "" : "s")),
                        Prefix.OPENMC, MessageType.WARNING);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Title title = Title.title(TranslationManager.translation("command.utils.restart.restart"),
                            TranslationManager.translation("command.utils.restart.in",
                                    Component.text(remainingTime).color(NamedTextColor.LIGHT_PURPLE),
                                    Component.text(remainingTime == 1 ? "" : "s")));
                    player.showTitle(title);

                    player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 0.4F);
                }
                remainingTime -= 1;
            }
        }.runTaskTimer(plugin, 20, 20);
    }
}

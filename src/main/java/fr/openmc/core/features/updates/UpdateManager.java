package fr.openmc.core.features.updates;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class UpdateManager {
    @Getter
    static Component message;

    public UpdateManager() {
        String version = OMCPlugin.getInstance().getDescription().getVersion();
        String milestoneUrl = "https://github.com/ServerOpenMC/PluginV2/releases/";

        message = Component.text("§8§m                                                     §r\n\n§7 Vous jouez actuellement sur la version")
            .append(Component.text("§d§l " + version).clickEvent(ClickEvent.openUrl(milestoneUrl)))
            .append(Component.text("§7 du plugin §d§lOpenMC.\n"))
            .append(Component.text("§f§l Cliquez ici pour voir les changements.").clickEvent(ClickEvent.openUrl(milestoneUrl)))
            .append(Component.text("\n\n§8§m                                                     §r"));

        long period = 14400 * 20; // 4h

        new BukkitRunnable() {
            @Override
            public void run() {
                sendUpdateBroadcast();
            };
        }.runTaskTimer(OMCPlugin.getInstance(), 0, period);
    }

    public static void sendUpdateMessage(Player player) {
        player.sendMessage(message);
    }

    public static void sendUpdateBroadcast() {
        Bukkit.broadcast(message);
    }
}

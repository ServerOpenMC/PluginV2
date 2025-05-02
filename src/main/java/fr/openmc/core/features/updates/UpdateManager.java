package fr.openmc.core.features.updates;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class UpdateManager {
    @Getter
    static UpdateManager instance;
    @Getter
    Component message;

    public UpdateManager() {
        instance = this;

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

    public void sendUpdateMessage(Player player) {
        player.sendMessage(message);
    }

    public void sendUpdateBroadcast() {
        Bukkit.broadcast(message);
    }
}

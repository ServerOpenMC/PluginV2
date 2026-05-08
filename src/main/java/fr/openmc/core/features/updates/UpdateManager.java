package fr.openmc.core.features.updates;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class UpdateManager extends Feature implements HasCommands, HasListeners {
    @Getter
    static Component message;

    @Override
    public void init() {
        String version = OMCPlugin.getInstance().getPluginMeta().getVersion();
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
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, period);
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new UpdateCommand()
        );
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new UpdateListener()
        );
    }

    public static void sendUpdateMessage(Player player) {
        player.sendMessage(message);
    }

    public static void sendUpdateBroadcast() {
        Bukkit.broadcast(message);
    }

}

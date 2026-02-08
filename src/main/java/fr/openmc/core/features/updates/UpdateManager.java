package fr.openmc.core.features.updates;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateManager {
    @Getter
    static Component message;

    public static void init() {
        String version = OMCPlugin.getInstance().getPluginMeta().getVersion();
        String milestoneUrl = "https://github.com/ServerOpenMC/PluginV2/releases/";

        message = Component.text("                                                     ", NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)
        .appendNewline()
        .appendNewline()
        .append(Component.text("Vous jouez actuellement sur la version ", NamedTextColor.GRAY))
        .append(Component.text(version, NamedTextColor.GREEN))
        .append(Component.text(" du plugin OpenMC.\n", NamedTextColor.GRAY))
        .append(Component.text("Cliquez ici pour voir les changements.", NamedTextColor.GREEN).clickEvent(ClickEvent.openUrl(milestoneUrl)))
        .appendNewline()
        .appendNewline()
        .append(Component.text("                                                     ", NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH));

        long period = 14400 * 20; // 4h

        new BukkitRunnable() {
            @Override
            public void run() {
                sendUpdateBroadcast();
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, period);
    }

    public static void sendUpdateMessage(Player player) {
        player.sendMessage(message);
    }

    public static void sendUpdateBroadcast() {
        Bukkit.broadcast(message);
    }
}

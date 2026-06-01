package fr.openmc.core.features.updates;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

        message = TranslationManager.translation("feature.updates.message.broadcast_version",
                Component.text(version, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
        ).clickEvent(ClickEvent.openUrl(milestoneUrl));

        long period = 14400 * 20;

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

package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;
import java.util.Set;

public class PlayerCommandListener implements Listener {
    private final Set<String> allowedCommands = Set.of(
            "/crafts",
            "/leave",
            "/ia omc_dream"
    );

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandExecution(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isDreamWorld(player.getWorld())) return;
        if (player.isOp()) return;

        String msg = event.getMessage().toLowerCase().trim();
        for (String cmd : allowedCommands) {
            if (msg.equals(cmd)) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isDreamWorld(player.getWorld())) return;
        if (player.isOp()) return;

        Collection<String> commands = event.getCommands();

        commands.clear();
        for (String cmd : allowedCommands) {
            commands.add(cmd.substring(1));
        }
    }
}

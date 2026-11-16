package fr.openmc.core.listeners;

import fr.openmc.api.menulib.events.OpenMenuEvent;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RestartListener implements Listener {
    @EventHandler
    public void onMenuOpen(OpenMenuEvent event) {
        if (!Restart.isRestarting) return;

        MessagesManager.sendMessage(event.getPlayer(),
                TranslationManager.translation("core.listener.cannot_open_inv"),
                Prefix.OPENMC,
                MessageType.INFO,
                false);
        event.setCancelled(true);
    }
}

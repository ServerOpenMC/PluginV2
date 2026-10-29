package fr.openmc.core.features.events.contents.open;

import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.core.OMCRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OpenListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!OpenManager.isOpeningActive()) return;

        OMCRegistry.CUSTOM_AMBIENTS.OPEN.apply(event.getPlayer(), true);
    }

    @EventHandler
    public void onCooldownEnd(CooldownEndEvent event) {
        if (!event.getGroup().equals(OpenManager.COOLDOWN_NAME)) return;

        OpenManager.endOpening();
    }
}

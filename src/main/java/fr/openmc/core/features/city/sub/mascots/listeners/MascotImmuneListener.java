package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.api.cooldown.CooldownEndEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MascotImmuneListener implements Listener {

    @EventHandler
    void onMascotImmune(CooldownEndEvent event) {
        System.out.println(event.getGroup() + " " + event.getUUID());
    }
}

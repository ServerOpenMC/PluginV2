package fr.openmc.core.registry.lootboxes.listener;

import fr.openmc.core.OMCPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class DesactivateFireworkDamageListener implements Listener {
    public static final NamespacedKey NO_DAMAGE_KEY = new NamespacedKey(OMCPlugin.getInstance(), "no_damage_firework");

    @EventHandler
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Firework firework)) return;

        if (firework.getPersistentDataContainer().has(NO_DAMAGE_KEY, PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);

        }
    }
}

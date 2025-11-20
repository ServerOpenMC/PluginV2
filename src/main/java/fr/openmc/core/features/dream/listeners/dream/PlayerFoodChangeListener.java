package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerFoodChangeListener implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (DreamUtils.isInDream(player)) {
            event.setCancelled(true);

            player.setFoodLevel(20);
            player.setSaturation(10.0f);
        }
    }
}

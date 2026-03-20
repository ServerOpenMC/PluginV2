package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerFoodChangeListener implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!DreamUtils.isInDreamWorld(player)) return;
        DBDreamPlayer dbDreamPlayer = DreamManager.getCacheDreamPlayer(player);

        // des que le joueur a acces a la vallée des nuages, il pourra perdre de la nourriture
        if (dbDreamPlayer == null || dbDreamPlayer.getProgressionOrb() < PlayerObtainOrb.SOUL_FOREST_ORB) {
            event.setCancelled(true);

            player.setFoodLevel(20);
            player.setSaturation(10.0f);
        }
    }
}

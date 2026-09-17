package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.registries.DreamOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerFoodChangeListener implements Listener {
    private final DreamManager dreamManager;

    public PlayerFoodChangeListener(DreamManager manager) {
        this.dreamManager = manager;
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!DreamUtils.isInDreamWorld(player)) return;
        DBDreamPlayer dbDreamPlayer = dreamManager.getCacheDreamPlayer(player);

        // des que le joueur a acces a la vallée des nuages, il pourra perdre de la nourriture
        if (dbDreamPlayer == null || dbDreamPlayer.getProgressionOrb() < DreamOrb.SOUL_FOREST_ORB.getStepInt()) {
            event.setCancelled(true);

            player.setFoodLevel(20);
            player.setSaturation(10.0f);
        }
    }
}

package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamDimensionManager;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerEatSomnifere implements Listener {
    private final DreamManager dreamManager;

    public PlayerEatSomnifere(DreamManager manager) {
        this.dreamManager = manager;
    }

    @EventHandler
    public void onFoodEated(PlayerItemConsumeEvent event) {
        DreamItem dreamItem = OMCRegistry.DREAM_ITEM.getByItemStack(event.getItem());

        if (dreamItem == null || !dreamItem.getId().equals(OMCRegistry.DREAM_ITEM.SOMNIFERE.getId())) return;

        Player player = event.getPlayer();
        if (!OMCRegistry.FEATURES.DIMENSION_OPENER.get().checkAccess(player, DreamDimensionManager.DIMENSION_NAME, event)) return;

        // somnifere se stack par 1, aucun check est nécessaire
        event.setItem(null);

        if (DreamUtils.isInDreamWorld(player)) {
            AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);

            if (attribute == null) return;

            player.setHealth(attribute.getValue());

            DreamPlayer dreamPlayer = dreamManager.getDreamPlayer(player);

            if (dreamPlayer == null) return;

            dreamPlayer.addTime(60L);
        } else {
            DBDreamPlayer dbDreamPlayer = dreamManager.getCacheDreamPlayer(player);

            if (dbDreamPlayer == null || (dbDreamPlayer.getDreamX() == null || dbDreamPlayer.getDreamY() == null || dbDreamPlayer.getDreamZ() == null)) {
                dreamManager.tpPlayerDream(player);
            } else {
                dreamManager.tpPlayerToLastDreamLocation(player);
            }
        }
    }
}

package fr.openmc.core.features.dream.listeners.others;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerEatSomnifere implements Listener {
    @EventHandler
    public void onFoodEated(PlayerItemConsumeEvent event) {
        DreamItem dreamItem = DreamItemRegistry.getByItemStack(event.getItem());

        if (dreamItem == null || !dreamItem.getName().equals("omc_dream:somnifere")) return;

        Player player = event.getPlayer();

        // somnifere se stack par 1, aucun check est nécessaire
        event.setItem(null);

        if (DreamUtils.isInDreamWorld(player)) {
            AttributeInstance attribute = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);

            if (attribute == null) return;

            player.setHealth(attribute.getValue());

            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

            if (dreamPlayer == null) return;

            dreamPlayer.addTime(60L);
        } else {
            DBDreamPlayer dbDreamPlayer = DreamManager.getCacheDreamPlayer(player);

            if (dbDreamPlayer == null || (dbDreamPlayer.getDreamX() == null || dbDreamPlayer.getDreamY() == null || dbDreamPlayer.getDreamZ() == null)) {
                DreamManager.tpPlayerDream(player);
            } else {
                DreamManager.tpPlayerToLastDreamLocation(player);
            }
        }
    }
}

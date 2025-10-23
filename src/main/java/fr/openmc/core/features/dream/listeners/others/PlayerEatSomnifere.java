package fr.openmc.core.features.dream.listeners.others;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.items.DreamItem;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import fr.openmc.core.features.dream.models.DBDreamPlayer;
import fr.openmc.core.features.dream.models.DreamPlayer;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerEatSomnifere implements Listener {
    @EventHandler
    public void onFoodEated(PlayerItemConsumeEvent event) {
        DreamItem dreamItem = DreamItemRegister.getByItemStack(event.getItem());
        if (dreamItem == null || !dreamItem.getName().equals("omc_dream:somnifere")) return;

        Player player = event.getPlayer();

        if (player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) {
            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

            if (dreamPlayer == null) return;

            dreamPlayer.addTime(60L);
            AttributeInstance attribute = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);

            if (attribute == null) return;

            player.setHealth(attribute.getValue());
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

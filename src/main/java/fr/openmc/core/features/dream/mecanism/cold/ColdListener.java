package fr.openmc.core.features.dream.mecanism.cold;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.DreamBiome;
import fr.openmc.core.features.dream.registries.DreamBiomesRegistry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public class ColdListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onGlaciteGrottoEntered(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (DreamBiomesRegistry.isInDreamBiome(player, DreamBiome.GLACITE_GROTTO)) {
            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
            if (dreamPlayer == null) return;

            if (dreamPlayer.getColdTask() != null) return;

            dreamPlayer.scheduleColdTask();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireBurnBlock(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (!DreamUtils.isDreamWorld(block.getWorld())) return;

        event.setCancelled(true);
    }
}

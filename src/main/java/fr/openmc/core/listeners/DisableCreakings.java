package fr.openmc.core.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.CreakingHeart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class DisableCreakings implements Listener {
    @EventHandler
    public void onHeartUpdate(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.CREAKING_HEART) return;
        CreakingHeart heart = (CreakingHeart) block.getBlockData();
        if (!heart.isNatural()) {
            heart.setActive(false);
            block.setBlockData(heart);
        }
    }
}

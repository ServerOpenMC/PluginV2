package fr.openmc.core.features.events.contents.christmas.mecanism.gift.listeners;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.GiftManager;
import fr.openmc.core.utils.world.block.BlockPos;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GiftBreakListener implements Listener {
    @EventHandler
    public void onGiftBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.ICE)) return;

        GiftManager.breakGift(BlockPos.of(block), OMCPlayer.of(event.getPlayer()));
    }
}

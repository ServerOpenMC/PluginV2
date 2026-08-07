package fr.openmc.core.features.chatanimations.contents.challenge;

import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.features.chatanimations.ChatAnimationManager;
import fr.openmc.core.features.chatanimations.contents.challenge.types.MineBlocksChallenge;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ChallengeListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof MineBlocksChallenge challenge)) return;
        if (animation.isFinished()) return;
        if (event.getBlock().getType() != challenge.getMaterial()) return;

        Player player = event.getPlayer();
        int count = challenge.getProgress().merge(player.getUniqueId(), 1, Integer::sum);

        if (count >= challenge.getTarget()) {
            Player winner = event.getPlayer();
            animation.complete(winner);
        }
    }
}

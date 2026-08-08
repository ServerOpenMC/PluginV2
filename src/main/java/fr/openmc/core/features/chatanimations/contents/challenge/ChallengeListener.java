package fr.openmc.core.features.chatanimations.contents.challenge;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.features.chatanimations.ChatAnimationManager;
import fr.openmc.core.features.chatanimations.contents.challenge.types.*;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ChallengeListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof MineBlocksChallenge challenge)) return;
        if (animation.isFinished()) return;
        if (!challenge.getKeyBlock().matches(event.getBlock())) return;

        Player player = event.getPlayer();
        int count = challenge.getProgress().merge(player.getUniqueId(), 1, Integer::sum);

        if (count >= challenge.getTarget()) {
            Player winner = event.getPlayer();
            animation.complete(winner);
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof JumpChallenge challenge)) return;
        if (animation.isFinished()) return;

        Player player = event.getPlayer();
        int count = challenge.getProgress().merge(player.getUniqueId(), 1, Integer::sum);

        if (count >= challenge.getTarget()) {
            animation.complete(player);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof KillEntityChallenge challenge)) return;
        if (animation.isFinished()) return;
        if (event.getEntity().getType() != challenge.getEntityType()) return;

        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        int count = challenge.getProgress().merge(killer.getUniqueId(), 1, Integer::sum);

        if (count >= challenge.getTarget()) {
            animation.complete(killer);
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof CraftItemChallenge challenge)) return;
        if (animation.isFinished()) return;
        if (!ItemUtils.isSimilar(event.getRecipe().getResult(), challenge.getItem())) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int count;
        if (!event.isShiftClick()) {
            count = challenge.getProgress().merge(player.getUniqueId(), 1, Integer::sum);
        } else {
            int maxCraftable = ItemUtils.getMaxCraftAmount(event.getInventory());
            int capacity = ItemUtils.getFreePlacesForItem((Player) event.getWhoClicked(), challenge.getItem());

            maxCraftable = Math.min(maxCraftable, capacity);
            if (maxCraftable == 0) return;

            count = challenge.getProgress().merge(player.getUniqueId(), maxCraftable, Integer::sum);
        }
        if (count >= challenge.getTarget()) {
            animation.complete(player);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof FishingChallenge challenge)) return;
        if (animation.isFinished()) return;

        Player player = event.getPlayer();
        int count = challenge.getProgress().merge(player.getUniqueId(), 1, Integer::sum);

        if (count >= challenge.getTarget()) {
            animation.complete(player);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof PlaceBlocksChallenge challenge)) return;
        if (animation.isFinished()) return;
        if (!challenge.getKeyBlock().matches(event.getBlock())) return;

        Player player = event.getPlayer();
        int count = challenge.getProgress().merge(player.getUniqueId(), 1, Integer::sum);

        if (count >= challenge.getTarget()) {
            animation.complete(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().distanceSquared(event.getTo()) == 0) return;

        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof WalkDistanceChallenge challenge)) return;
        if (animation.isFinished()) return;

        Player player = event.getPlayer();
        double distance = event.getFrom().distance(event.getTo());
        double total = challenge.getProgress().merge(player.getUniqueId(), distance, Double::sum);

        if (total >= challenge.getTarget()) {
            animation.complete(player);
        }
    }

}

package fr.openmc.core.features.dream.mecanism.cloudfishing;

import fr.openmc.core.OMCPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FishBiteTask extends BukkitRunnable {

    private final Player player;
    private final FishHook hook;

    public FishBiteTask(Player player, FishHook hook, long delay) {
        this.player = player;
        this.hook = hook;

        this.runTaskLater(OMCPlugin.getInstance(), delay);
    }

    @Override
    public void run() {
        if (CloudFishingManager.getHookedPlayers().containsKey(player.getUniqueId())) {
            endBite();
            player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 1f);
        }
    }

    public void endBite() {
        CloudFishingManager.getHookedPlayers().remove(player.getUniqueId());
        hook.remove();
        cancel();
    }
}

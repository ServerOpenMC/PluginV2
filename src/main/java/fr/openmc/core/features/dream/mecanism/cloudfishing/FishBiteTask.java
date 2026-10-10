package fr.openmc.core.features.dream.mecanism.cloudfishing;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
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
        if (OMCRegistry.DREAM_FEATURES.CLOUD_FISHING.getHookedPlayers().containsKey(player.getUniqueId())) {
            endBite();
            player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 1f);
        }
    }

    public void endBite() {
        OMCRegistry.DREAM_FEATURES.CLOUD_FISHING.getHookedPlayers().remove(player.getUniqueId());
        hook.remove();
        cancel();
    }
}

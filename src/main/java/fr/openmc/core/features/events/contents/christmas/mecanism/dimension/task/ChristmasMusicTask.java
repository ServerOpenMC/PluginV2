package fr.openmc.core.features.events.contents.christmas.mecanism.dimension.task;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasDimensionManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ChristmasMusicTask implements Listener {
    private static final Key MUSIC = Key.key("omc_sounds", "christmas");

    private final Map<UUID, Long> nextPlay = new HashMap<>();
    private long tick = 0;
    private BukkitTask task;

    public void start() {
        OMCPlugin.registerEvents(ChristmasMusicTask::new);
        task = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(),
                this::run, 20L, 20L);
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    private void run() {
        tick += 20;

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();

            if (!player.getWorld().equals(ChristmasDimensionManager.CHRISTMAS_WORLD)) continue;

            long next = nextPlay.computeIfAbsent(id,
                    _ -> tick + ThreadLocalRandom.current().nextLong(600, 3600));

            if (tick < next) continue;

            player.playSound(Sound.sound(MUSIC, Sound.Source.MUSIC, 1f, 1f));

            long delay = ThreadLocalRandom.current().nextLong(12000, 24000);
            nextPlay.put(id, tick + delay);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        nextPlay.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        nextPlay.remove(player.getUniqueId());
        player.stopSound(SoundStop.namedOnSource(MUSIC, Sound.Source.MUSIC));
    }
}
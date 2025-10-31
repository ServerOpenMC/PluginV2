package fr.openmc.core.features.dream.listeners.biomes;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerEnteredBiome implements Listener {

    private static final List<Biome> BIOME_ORDER = List.of(
            DreamBiome.SCULK_PLAINS.getBiome(),
            DreamBiome.SOUL_FOREST.getBiome(),
            DreamBiome.CLOUD_LAND.getBiome(),
            DreamBiome.MUD_BEACH.getBiome(),
            DreamBiome.GLACITE_GROTTO.getBiome()
    );

    private static final List<String> ORB_UNLOCKER = List.of(
            "",
            "Orbe de Domination",
            "Orbe des Ames",
            "Orbe des Nuages",
            "Orbe de Boue"
    );

    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isInDream(player)) return;

        Biome biome = player.getLocation().getBlock().getBiome();
        int index = BIOME_ORDER.indexOf(biome);
        if (index == -1) return;

        DBDreamPlayer cacheData = DreamManager.getCacheDreamPlayer(player);
        int unlocked = cacheData == null ? 0 : cacheData.getProgressionOrb();

        if (index > unlocked) {
            if (!activeTasks.containsKey(player.getUniqueId())) {
                BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                        OMCPlugin.getInstance(),
                        () -> {
                            if (!player.isOnline()) {
                                stopTask(player);
                                return;
                            }

                            Biome current = player.getLocation().getBlock().getBiome();
                            int idx = BIOME_ORDER.indexOf(current);
                            if (idx == -1 || idx <= unlocked) {
                                stopTask(player);
                                return;
                            }

                            applyFog(player);
                            spawnParticles(player);
                        },
                        0L, 40L
                );

                activeTasks.put(player.getUniqueId(), task);
                MessagesManager.sendMessage(player, Component.text("Attention, vous êtes dans un biome que vous avez pas encore débloqué, il vous faut l'§b" + ORB_UNLOCKER.get(index)), Prefix.DREAM, MessageType.WARNING, false);
            }
        } else {
            stopTask(player);
        }
    }

    private void stopTask(Player player) {
        BukkitTask task = activeTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }

    private void applyFog(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1, true, false));
    }

    private void spawnParticles(Player player) {
        player.getWorld().spawnParticle(
                Particle.CLOUD,
                player.getLocation(),
                100, 1, 1, 1, 0.1
        );
    }
}

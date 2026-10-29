package fr.openmc.core.features.events.contents.open;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class OpenManager extends Feature implements HasCommands, HasListeners {
    @Getter
    private static Player starter;
    static final String COOLDOWN_NAME = "opening";
    private static final long COOLDOWN_DURATION = 60 * 60 * 1000L;

    private static final Random RANDOM = ThreadLocalRandom.current();
    private static final Color[] COLORS = {
            Color.RED, Color.ORANGE, Color.YELLOW, Color.PURPLE, Color.AQUA, Color.FUCHSIA, Color.WHITE
    };
    private static BukkitTask fireworkTask;


    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new OpenCommand()
        );
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(OpenListener::new);
    }

    public static void startOpening(Player starter) {
        DynamicCooldownManager.use(starter.getUniqueId(), COOLDOWN_NAME, COOLDOWN_DURATION);
        OpenManager.starter = starter;

        OMCRegistry.CUSTOM_AMBIENTS.OPEN.apply(new HashSet<>(Bukkit.getOnlinePlayers()));
        playIntro();
        startFireworks();
    }

    public static void endOpening() {
        OpenManager.starter = null;

        OMCRegistry.CUSTOM_AMBIENTS.OPEN.reset(new HashSet<>(Bukkit.getOnlinePlayers()));
        stopFireworks();
    }

    public static boolean isOpeningActive() {
        return starter != null;
    }

    public static void playIntro() {
        Location spawn = SpawnManager.getSpawnLocation();
        World world = spawn.getWorld();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(Title.title(
                    Component.text("OUVERTURE", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD),
                    Component.text("Bienvenue sur le serveur !", NamedTextColor.DARK_PURPLE),
                    Title.Times.times(Duration.ofMillis(200), Duration.ofSeconds(4), Duration.ofSeconds(1))
            ));
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }

        world.strikeLightningEffect(spawn);
        world.playSound(spawn, Sound.ENTITY_ENDER_DRAGON_GROWL, 8f, 1f);
        ParticleUtils.sendParticlePacket(
                spawn.getNearbyEntitiesByType(Player.class, 64),
                Particle.GLOW, spawn.clone().add(0, 2, 0),
                300, 3, 3, 3, 0.5, null
        );

        for (int i = 0; i < 12; i++) {
            double angle = 2 * Math.PI * i / 12;
            Location loc = spawn.clone().add(Math.cos(angle) * 10, 0, Math.sin(angle) * 10);
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(),
                    () -> launchFirework(loc), i * 5L);
        }
    }

    public static void startFireworks() {
        stopFireworks();

        fireworkTask = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            if (!OpenManager.isOpeningActive()) {
                stopFireworks();
                return;
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                double angle = RANDOM.nextDouble() * 2 * Math.PI;
                double distance = 10 + RANDOM.nextInt(10);
                int x = (int) (p.getLocation().getX() + Math.cos(angle) * distance);
                int z = (int) (p.getLocation().getZ() + Math.sin(angle) * distance);

                World world = p.getWorld();
                if (!world.isChunkLoaded(x >> 4, z >> 4)) continue;

                launchFirework(new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 1, z + 0.5));
            }
        }, 20L * 30, 20L * 30);
    }

    public static void stopFireworks() {
        if (fireworkTask != null) {
            fireworkTask.cancel();
            fireworkTask = null;
        }
    }

    private static void launchFirework(Location loc) {
        loc.getWorld().spawn(loc, Firework.class, firework -> {
            FireworkMeta meta = firework.getFireworkMeta();
            FireworkEffect.Type[] types = FireworkEffect.Type.values();
            meta.addEffect(FireworkEffect.builder()
                    .with(types[RANDOM.nextInt(types.length)])
                    .withColor(COLORS[RANDOM.nextInt(COLORS.length)], COLORS[RANDOM.nextInt(COLORS.length)])
                    .withFade(COLORS[RANDOM.nextInt(COLORS.length)])
                    .flicker(RANDOM.nextBoolean())
                    .trail(true)
                    .build());
            meta.setPower(1 + RANDOM.nextInt(2));
            firework.setFireworkMeta(meta);
        });
    }

}

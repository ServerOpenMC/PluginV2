package fr.openmc.core.features.displays.bossbar;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.displays.bossbar.commands.BossBarCommand;
import fr.openmc.core.features.displays.bossbar.contents.MainBossbar;
import fr.openmc.core.features.displays.scoreboards.BaseScoreboard;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class BossbarManager {
    private static final List<BaseBossbar> registeredBossbar = new ArrayList<>();

    private static final Map<UUID, Map<String, BossBar>> activeBossbars = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> lastUpdate = new HashMap<>();
    private static final Map<UUID, Set<String>> offBossbars = new HashMap<>();

    public static void init() {
        CommandsManager.getHandler().register(new BossBarCommand());

        registerBossbars(
                new MainBossbar(),
                new DreamBossBar()
        );

        start();
    }

    public static void registerBossbars(BaseBossbar... bossbar) {
        registeredBossbar.addAll(Arrays.asList(bossbar));
        registeredBossbar.sort(Comparator.comparingInt(BaseBossbar::weight).reversed());
    }

    private static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updatePlayer(player);
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
    }


    private static void updatePlayer(Player player) {

        UUID uuid = player.getUniqueId();

        activeBossbars.putIfAbsent(uuid, new HashMap<>());
        offBossbars.putIfAbsent(uuid, new HashSet<>());
        lastUpdate.putIfAbsent(uuid, new HashMap<>());

        Map<String, BossBar> playerBars = activeBossbars.get(uuid);
        Set<String> toggled = offBossbars.get(uuid);
        Map<String, Long> playerLastUpdate = lastUpdate.get(uuid);

        long now = System.currentTimeMillis();

        for (BaseBossbar base : registeredBossbar) {

            String id = base.id();

            if (toggled.contains(id)) {
                removeBossBar(player, id);
                continue;
            }

            boolean shouldDisplay = base.shouldDisplay(player);
            BossBar existing = playerBars.get(id);

            if (shouldDisplay) {

                if (existing == null) {
                    existing = BossBar.bossBar(
                            Component.text(),
                            1f,
                            base.color(player),
                            base.style(player)
                    );

                    player.showBossBar(existing);
                    playerBars.put(id, existing);

                    base.update(player, existing);
                    playerLastUpdate.put(id, now);
                    continue;
                }

                long last = playerLastUpdate.getOrDefault(id, 0L);
                long intervalMillis = base.updateInterval() * 1000L;

                if (now - last >= intervalMillis) {
                    base.update(player, existing);
                    playerLastUpdate.put(id, now);
                }

            } else {
                removeBossBar(player, id);
                playerLastUpdate.remove(id);
            }
        }
    }

    public static void addBossBar(Player player, String id) {
        BaseBossbar base = getRegistered(id);
        if (base == null) return;

        activeBossbars.putIfAbsent(player.getUniqueId(), new HashMap<>());

        Map<String, BossBar> playerBars = activeBossbars.get(player.getUniqueId());

        if (playerBars.containsKey(id)) return;

        BossBar bar = BossBar.bossBar(
                Component.text(),
                1L,
                base.color(player),
                base.style(player)
        );

        player.showBossBar(bar);
        playerBars.put(id, bar);
    }

    public static void removeBossBar(Player player, String id) {
        Map<String, BossBar> bars = activeBossbars.get(player.getUniqueId());
        if (bars == null) return;

        BossBar bar = bars.remove(id);

        if (bar != null) {
            player.hideBossBar(bar);
        }
    }

    public static BossBar getBossBar(Player player, String id) {
        Map<String, BossBar> bars = activeBossbars.get(player.getUniqueId());
        if (bars == null) return null;
        return bars.get(id);
    }

    public static void toggleBossBar(Player player, String id) {
        offBossbars.putIfAbsent(player.getUniqueId(), new HashSet<>());

        Set<String> toggled = offBossbars.get(player.getUniqueId());

        if (toggled.contains(id)) {
            toggled.remove(id);
        } else {
            toggled.add(id);
            removeBossBar(player, id);
        }
    }

    public static void toggleAllBossBar(Player player) {
        for (BaseBossbar baseBossbar : registeredBossbar) {
            toggleBossBar(player, baseBossbar.id());
        }
    }

    public static boolean isToggled(Player player, String id) {
        return offBossbars.getOrDefault(player.getUniqueId(), Set.of()).contains(id);
    }

    public static void removePlayer(Player player) {
        Map<String, BossBar> bars = activeBossbars.remove(player.getUniqueId());
        if (bars != null) {
            for (BossBar bar : bars.values()) {
                player.hideBossBar(bar);
            }
        }

        offBossbars.remove(player.getUniqueId());
    }

    private static BaseBossbar getRegistered(String id) {
        return registeredBossbar.stream()
                .filter(b -> b.id().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }
}

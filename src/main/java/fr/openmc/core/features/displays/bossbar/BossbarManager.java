package fr.openmc.core.features.displays.bossbar;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.features.displays.bossbar.commands.BossBarCommand;
import fr.openmc.core.features.displays.bossbar.contents.MainBossbar;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Gère l'enregistrement, l'affichage et la mise à jour des boss bars.
 */
public class BossbarManager extends Feature {
    private static final List<BaseBossbar> registeredBossbar = new ArrayList<>();

    private static final Map<UUID, Map<String, BossBar>> activeBossbars = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> lastUpdate = new HashMap<>();
    private static final Map<UUID, Set<String>> offBossbars = new HashMap<>();

    @Override
    public void init() {
        CommandsManager.getHandler().register(new BossBarCommand());

        registerBossbars(
                new MainBossbar(),
                new DreamBossBar()
        );

        start();
    }

    @Override
    public void save() {
        // nothing to save
    }

    /**
     * Enregistre une liste de boss bars et les trie par poids décroissant.
     *
     * @param bossbar Les boss bars à enregistrer
     */
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

        List<String> orderedIds = new ArrayList<>();

        // * Determination des bossbar a afficher
        for (BaseBossbar base : registeredBossbar) {
            String id = base.id();

            if (toggled.contains(id)) {
                removeBossBar(player, id);
                continue;
            }

            boolean shouldDisplay = base.shouldDisplay(player);
            BossBar existing = playerBars.get(id);

            if (shouldDisplay) {
                orderedIds.add(id);

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

        // * Sécurité afin d'assurer que les bossbar qui ne sont plus dans la liste des bossbar à afficher soient supprimé
        for (String id : new ArrayList<>(playerBars.keySet())) {
            if (!orderedIds.contains(id)) {
                removeBossBar(player, id);
                playerLastUpdate.remove(id);
            }
        }

        // * Tri des boss bar pour l'affichage
        for (String id : orderedIds) {
            BossBar bar = playerBars.get(id);
            if (bar != null) {
                player.hideBossBar(bar);
                player.showBossBar(bar);
            }
        }
    }

    /**
     * Retire une boss bar d'un joueur.
     *
     * @param player Le joueur
     * @param id L'identifiant de la boss bar
     */
    public static void removeBossBar(Player player, String id) {
        Map<String, BossBar> bars = activeBossbars.get(player.getUniqueId());
        if (bars == null) return;

        BossBar bar = bars.remove(id);

        if (bar != null) {
            player.hideBossBar(bar);
        }
    }

    /**
     * Retourne la boss bar active d'un joueur pour un identifiant donné.
     *
     * @param player Le joueur
     * @param id L'identifiant de la boss bar
     * @return La boss bar active, ou null si absente
     */
    public static BossBar getBossBar(Player player, String id) {
        Map<String, BossBar> bars = activeBossbars.get(player.getUniqueId());
        if (bars == null) return null;
        return bars.get(id);
    }

    /**
     * Active ou désactive une boss bar pour un joueur.
     *
     * @param player Le joueur
     * @param id L'identifiant de la boss bar
     */
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

    /**
     * Active ou désactive toutes les boss bars pour un joueur.
     *
     * @param player Le joueur
     */
    public static void toggleAllBossBar(Player player) {
        for (BaseBossbar baseBossbar : registeredBossbar) {
            toggleBossBar(player, baseBossbar.id());
        }
    }

    /**
     * Indique si une boss bar est désactivée pour un joueur.
     *
     * @param player Le joueur
     * @param id L'identifiant de la boss bar
     * @return true si la boss bar est désactivée, false sinon
     */
    public static boolean isToggled(Player player, String id) {
        return offBossbars.getOrDefault(player.getUniqueId(), Set.of()).contains(id);
    }

    /**
     * Supprime toutes les boss bars et l'état de toggle pour un joueur.
     *
     * @param player Le joueur
     */
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

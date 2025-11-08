package fr.openmc.core.features.displays.scoreboards;

import fr.openmc.api.hooks.LuckPermsHook;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.api.scoreboard.repository.ObjectCacheRepository;
import fr.openmc.api.scoreboard.repository.impl.ObjectCacheRepositoryImpl;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.displays.scoreboards.sb.CityWarScoreboard;
import fr.openmc.core.features.displays.scoreboards.sb.MainScoreboard;
import fr.openmc.core.features.displays.scoreboards.sb.RestartScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public class ScoreboardManager implements Listener {
    public static final ObjectCacheRepository<SternalBoard> boardCache = new ObjectCacheRepositoryImpl();
    private static final List<BaseScoreboard> scoreboards = new ArrayList<>();
    private static GlobalTeamManager globalTeamManager;

    private static final Map<UUID, Map<BaseScoreboard, Long>> lastUpdate = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(new ScoreboardListener());

        registerScoreboard(
                new MainScoreboard(),
                new RestartScoreboard(),
                new CityWarScoreboard()
        );

        Bukkit.getScheduler().runTaskTimer(
                OMCPlugin.getInstance(),
                ScoreboardManager::updateAllBoards,
                0L,
                20L // every second
        );

        if (LuckPermsHook.isHasLuckPerms())
            globalTeamManager = new GlobalTeamManager(boardCache);
    }

    public static void updateAllBoards() {
        long now = System.currentTimeMillis();

        Bukkit.getOnlinePlayers().forEach(player -> {
            BaseScoreboard active = null;
            for (BaseScoreboard sb : scoreboards) {
                if (sb.shouldDisplay(player)) {
                    active = sb;
                    break;
                }
            }

            if (active == null) return;

            Map<BaseScoreboard, Long> playerUpdates = lastUpdate.computeIfAbsent(
                    player.getUniqueId(),
                    k -> new HashMap<>()
            );

            long last = playerUpdates.getOrDefault(active, 0L);
            if (now - last < active.updateInterval() * 1000L) return;


            SternalBoard board = boardCache.find(player.getUniqueId()) == null ? createNewBoard(player) : boardCache.find(player.getUniqueId());

            active.update(player, board);
            playerUpdates.put(active, now);

            if (LuckPermsHook.isHasLuckPerms() && globalTeamManager != null) {
                globalTeamManager.updatePlayerTeam(player);
            }
        });
    }

    public static SternalBoard createNewBoard(Player player) {
        SternalBoard board = new SternalBoard(player);
        updateBoard(player, board);
        boardCache.create(board);
        return board;
    }

    public static void updateBoard(Player player, SternalBoard board) {
        for (BaseScoreboard scoreboard : scoreboards) {
            if (scoreboard.shouldDisplay(player)) {
                scoreboard.init(player, board);
                break;
            }
        }

        if (LuckPermsHook.isHasLuckPerms() && globalTeamManager != null) {
            globalTeamManager.updatePlayerTeam(player);
        }
    }

    public static void registerScoreboard(BaseScoreboard... scoreboard) {
        scoreboards.addAll(Arrays.asList(scoreboard));
        scoreboards.sort(Comparator.comparingInt(BaseScoreboard::priority).reversed());
    }

    public static void cleanupPlayer(UUID playerUUID) {
        lastUpdate.remove(playerUUID);
        boardCache.delete(playerUUID);
    }
}
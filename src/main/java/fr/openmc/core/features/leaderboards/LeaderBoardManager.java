package fr.openmc.core.features.leaderboards;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.features.types.NotLoadInUnitTest;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.leaderboards.commands.LeaderBoardCommands;
import fr.openmc.core.features.leaderboards.leaderboards.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Credit(developers = {"ElitGaimix"})
public class LeaderBoardManager extends Feature implements NotLoadInUnitTest, LoadAfterItemsAdder, HasCommands {

    public static CityMoneyLeaderBoard cityMoneyLeaderBoard;
    public static ContributorsLeaderBoard contributorsLeaderBoard;
    public static MoneyLeaderBoard moneyLeaderBoard;
    public static PlayTimeLeaderBoard playTimeLeaderBoard;
    public static PumpkinCountLeaderBoard pumpkinCountLeaderBoard;

    private final static List<LeaderBoard> enabledLeaderBoards = new ArrayList<>();

    private static File leaderBoardConfig = null;

    private static BukkitTask viewerTimer;

    @Override
    public void init() {
        cityMoneyLeaderBoard = new CityMoneyLeaderBoard();
        contributorsLeaderBoard = new ContributorsLeaderBoard();
        moneyLeaderBoard = new MoneyLeaderBoard();
        playTimeLeaderBoard = new PlayTimeLeaderBoard();
        pumpkinCountLeaderBoard = new PumpkinCountLeaderBoard();
        start();
    }

    public static Stream<String> getLeaderBoards(){
        return enabledLeaderBoards.stream().map(LeaderBoard::getId);
    }

    public static Optional<LeaderBoard> getLeaderBoard(String id){
        return enabledLeaderBoards.stream().filter(lb -> lb.getId().equalsIgnoreCase(id)).findAny();
    }

    public static void stop() {
        enabledLeaderBoards.forEach(LeaderBoard::remove);
        enabledLeaderBoards.clear();
        if (viewerTimer != null){
            viewerTimer.cancel();
            viewerTimer = null;
        }
    }

    public static void start(){
        registerLeaderBoard(cityMoneyLeaderBoard);
        registerLeaderBoard(contributorsLeaderBoard);
        registerLeaderBoard(moneyLeaderBoard);
        registerLeaderBoard(playTimeLeaderBoard);
        registerLeaderBoard(pumpkinCountLeaderBoard);

        viewerTimer = new BukkitRunnable() {
            @Override
            public void run() {
                updateViewers();
            }
        }.runTaskTimer(OMCPlugin.getInstance(),0L,20L);
    }

    public static void reload() {
        stop();
        start();
    }

    public static void update() {
        enabledLeaderBoards.forEach(LeaderBoard::update);
    }

    public static void updateViewers() {
        enabledLeaderBoards.forEach(LeaderBoard::updateViewers);
    }

    public static void refreshViewer(Player player) {
        enabledLeaderBoards.forEach(leaderBoard -> leaderBoard.refreshViewer(player));
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new LeaderBoardCommands()
        );
    }

    public static File getLeaderBoardFile() {
        if (leaderBoardConfig == null) {
            OMCPlugin plugin = OMCPlugin.getInstance();
            if (plugin == null) throw new IllegalStateException("OMCPlugin instance not initialized");
            leaderBoardConfig = new File(plugin.getDataFolder(), "data/leaderboards.yml");
        }
        if (!leaderBoardConfig.exists()) {
            if (!leaderBoardConfig.getParentFile().exists() && !leaderBoardConfig.getParentFile().mkdirs())
                OMCLogger.error("Can't create folder : " + leaderBoardConfig.getParentFile());
            try {
                if (!leaderBoardConfig.createNewFile())
                    OMCLogger.error("Can't create leaderboard config : " + leaderBoardConfig);
            } catch (IOException e) {
                throw new IllegalStateException("Can't create leaderboard config : " + leaderBoardConfig, e);
            }
        }
        return leaderBoardConfig;
    }

    public static void registerLeaderBoard(LeaderBoard leaderBoard){
        if (enabledLeaderBoards.stream().noneMatch(existing -> existing.getId().equals(leaderBoard.getId()))) {
            enabledLeaderBoards.add(leaderBoard);
            leaderBoard.start();
        }
    }

    public static void unregisterLeaderBoard(LeaderBoard leaderBoard){
        if (enabledLeaderBoards.remove(leaderBoard))
            leaderBoard.remove();
    }

}

package fr.openmc.core.features.leaderboards;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.leaderboards.commands.LeaderBoardCommands;
import fr.openmc.core.features.leaderboards.leaderboards.*;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.NotLoadInUnitTest;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
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
public class LeaderBoardManager extends Feature implements NotLoadInUnitTest, HasCommands {

    public CityMoneyLeaderBoard CITY_MONEY_LEADERBOARD;
    public ContributorsLeaderBoard CONTRIBUTORS_LEADERBOARD;
    public MoneyLeaderBoard MONEY_LEADERBOARD;
    public PlayTimeLeaderBoard PLAYTIME_LEADERBOARD;
    public PumpkinCountLeaderBoard PUMPKIN_COUNT_LEADERBOARD;

    private final List<LeaderBoard> enabledLeaderBoards = new ArrayList<>();

    private File leaderBoardConfig = null;

    private BukkitTask viewerTimer;

    @Override
    public void init() {
        CITY_MONEY_LEADERBOARD = new CityMoneyLeaderBoard();
        CONTRIBUTORS_LEADERBOARD = new ContributorsLeaderBoard();
        MONEY_LEADERBOARD = new MoneyLeaderBoard();
        PLAYTIME_LEADERBOARD = new PlayTimeLeaderBoard();
        PUMPKIN_COUNT_LEADERBOARD = new PumpkinCountLeaderBoard();
        start();
    }

    public Stream<String> getLeaderBoards(){
        return enabledLeaderBoards.stream().map(LeaderBoard::getId);
    }

    public Optional<LeaderBoard> getLeaderBoard(String id){
        return enabledLeaderBoards.stream().filter(lb -> lb.getId().equalsIgnoreCase(id)).findAny();
    }

    public void stop() {
        enabledLeaderBoards.forEach(LeaderBoard::remove);
        enabledLeaderBoards.clear();
        if (viewerTimer != null){
            viewerTimer.cancel();
            viewerTimer = null;
        }
    }

    public void start(){
        registerLeaderBoard(CITY_MONEY_LEADERBOARD);
        registerLeaderBoard(CONTRIBUTORS_LEADERBOARD);
        registerLeaderBoard(MONEY_LEADERBOARD);
        registerLeaderBoard(PLAYTIME_LEADERBOARD);
        registerLeaderBoard(PUMPKIN_COUNT_LEADERBOARD);

        viewerTimer = new BukkitRunnable() {
            @Override
            public void run() {
                updateViewers();
            }
        }.runTaskTimer(OMCPlugin.getInstance(),0L,20L);
    }

    public void reload() {
        stop();
        start();
    }

    public void update() {
        enabledLeaderBoards.forEach(LeaderBoard::update);
    }

    public void updateViewers() {
        enabledLeaderBoards.forEach(LeaderBoard::updateViewers);
    }

    public void refreshViewer(Player player) {
        enabledLeaderBoards.forEach(leaderBoard -> leaderBoard.refreshViewer(player));
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new LeaderBoardCommands()
        );
    }

    public File getLeaderBoardFile() {
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

    public void registerLeaderBoard(LeaderBoard leaderBoard){
        if (enabledLeaderBoards.stream().noneMatch(existing -> existing.getId().equals(leaderBoard.getId()))) {
            enabledLeaderBoards.add(leaderBoard);
            leaderBoard.start();
        }
    }

    public void unregisterLeaderBoard(LeaderBoard leaderBoard){
        if (enabledLeaderBoards.remove(leaderBoard))
            leaderBoard.remove();
    }

}

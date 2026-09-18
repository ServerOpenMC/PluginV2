package fr.openmc.core.features.leaderboards;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.features.types.NotLoadInUnitTest;
import fr.openmc.core.bootstrap.integration.OMCLogger;import fr.openmc.core.features.leaderboards.commands.LeaderboardCommands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Credit(developers = {"ElitGaimix"})
public class LeaderBoardManager extends Feature implements NotLoadInUnitTest, LoadAfterItemsAdder, HasCommands {

    private final static List<LeaderBoard> leaderboards = new ArrayList<>();

    private static File leaderBoardConfig = null;

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new LeaderboardCommands()
        );
    }

    public static File getLeaderBoardFile() {
        if (leaderBoardConfig == null) {
            OMCPlugin plugin = OMCPlugin.getInstance();
            if (plugin == null) {
                throw new IllegalStateException("OMCPlugin instance not initialized");
            }
            leaderBoardConfig = new File(plugin.getDataFolder(), "data/leaderboards.yml");
        }
        if (!leaderBoardConfig.exists()) {
            if (!leaderBoardConfig.getParentFile().mkdirs())
                OMCLogger.error("Can't create folder : " + leaderBoardConfig.getParentFile());
            OMCPlugin.getInstance().saveResource(leaderBoardConfig.getPath(), false);
        }
        return leaderBoardConfig;
    }

    public static void registerLeaderBoard(LeaderBoard leaderBoard){
        if (!leaderboards.contains(leaderBoard))
            leaderboards.add(leaderBoard);
    }

    public static void unregisterLeaderBoard(LeaderBoard leaderBoard){
        leaderboards.remove(leaderBoard);
    }

}

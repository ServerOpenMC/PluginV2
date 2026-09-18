package fr.openmc.core.features.leaderboards;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.features.types.NotLoadInUnitTest;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.leaderboards.commands.LeaderboardCommands;
import fr.openmc.core.features.leaderboards.leaderboards.PumpkinCountLeaderBoard;
import fr.openmc.core.features.leaderboards.leaderboards.TestLeaderBoard;
import net.kyori.adventure.text.format.TextColor;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Credit(developers = {"ElitGaimix"})
public class LeaderBoardManager extends Feature implements NotLoadInUnitTest, LoadAfterItemsAdder, HasCommands {

    private final static List<LeaderBoard> leaderboards = new ArrayList<>();

    private static File leaderBoardConfig = null;

    private static BukkitTask taskTimer = null;

    //TODO : Temp, juste pour test, répartir les initialisation au bon endroits
    @Override
    public void init(){
        PumpkinCountLeaderBoard leaderBoard = new PumpkinCountLeaderBoard();
        leaderboards.add(leaderBoard);
        leaderboards.add(new TestLeaderBoard());
        taskTimer = new BukkitRunnable() {

            @Override
            public void run() {
                leaderboards.forEach(LeaderBoard::update);
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0, 20L);
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new LeaderboardCommands()
        );
    }

    public static void updateViewers(){
        leaderboards.forEach(LeaderBoard::updateViewers);
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

    //TODO : Trouver si elle est pas mieux autre part
    public static TextColor getRankColor(int rank) {
        return switch (rank) {
            case 1 -> TextColor.color(0xFFD700);
            case 2 -> TextColor.color(0xC0C0C0);
            case 3 -> TextColor.color(0x614E1A);
            default -> TextColor.color(0x4B4B4B);
        };
    }

    public static void registerLeaderBoard(LeaderBoard leaderBoard){
        if (!leaderboards.contains(leaderBoard))
            leaderboards.add(leaderBoard);
    }

    public static void unregisterLeaderBoard(LeaderBoard leaderBoard){
        leaderboards.remove(leaderBoard);
    }

}

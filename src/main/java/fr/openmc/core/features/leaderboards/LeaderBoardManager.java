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
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

    private final static List<LeaderBoard> leaderboards = new ArrayList<>();

    private static File leaderBoardConfig = null;

    private static BukkitTask viewerTimer;

    @Override
    public void init() {
        reload();
    }

    public static Stream<String> getLeaderBoards(){
        return leaderboards.stream().map(LeaderBoard::getId);
    }

    public static void setScale(float scale) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getLeaderBoardFile());
        config.set("scale", scale);
        config.save(getLeaderBoardFile());
        reload();
    }

    public static Optional<LeaderBoard> getLeaderBoard(String id){
        return leaderboards.stream().filter(lb -> lb.getId().equalsIgnoreCase(id)).findAny();
    }

    public static synchronized void stop() {
        leaderboards.forEach(LeaderBoard::remove);
        leaderboards.clear();

        viewerTimer.cancel();
        viewerTimer = null;
    }

    public static synchronized void reload() {
        stop();

        registerLeaderBoard(new CityMoneyLeaderBoard());
        registerLeaderBoard(new ContributorsLeaderBoard());
        registerLeaderBoard(new MoneyLeaderBoard());
        registerLeaderBoard(new PlayTimeLeaderBoard());
        registerLeaderBoard(new PumpkinCountLeaderBoard());
        viewerTimer = new BukkitRunnable() {
            @Override
            public void run() {
                updateViewers();
            }
        }.runTaskTimer(OMCPlugin.getInstance(),0L,20L);
    }

    public static void update() {
        leaderboards.forEach(LeaderBoard::update);
    }

    public static void updateViewers() {
        leaderboards.forEach(LeaderBoard::updateViewers);
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
        if (leaderboards.stream().noneMatch(existing -> existing.getId().equals(leaderBoard.getId()))) {
            leaderboards.add(leaderBoard);
            leaderBoard.start();
        }
    }

    public static void unregisterLeaderBoard(LeaderBoard leaderBoard){
        if (leaderboards.remove(leaderBoard)) {
            leaderBoard.remove();
        }
    }

}

package fr.openmc.core.features.events.contents.christmas;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.integration.DatabaseManager;
import fr.openmc.core.features.events.contents.christmas.mecanism.advent.commands.AdventCalendarCommands;
import fr.openmc.core.features.events.contents.christmas.mecanism.advent.models.AdventProgression;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ChristmasManager extends Feature implements HasDatabase, HasCommands {
    private final static Map<UUID, List<Integer>> playerProgressionCalendar = new HashMap<>();
    private static Dao<AdventProgression, UUID> adventCalendarProgressionDao;

    @Override
    public void init() {
        ChristmasDimensionManager.init();
        OMCPlugin.registerFeature(new RiftManager());
        loadPlayerProgression();
    }

    @Override
    public void save() {
        ChristmasDimensionManager.save();
        savePlayerProgression();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, AdventProgression.class);
        adventCalendarProgressionDao = DaoManager.createDao(connectionSource, AdventProgression.class);
    }

    // todo: faire gestion de la fin (clear db apres le 31 decembre)

    public static void claimReward(Player player, Integer day) {
        playerProgressionCalendar.computeIfAbsent(player.getUniqueId(), _ -> new ArrayList<>())
                .add(day);
    }

    public static boolean hasClaimReward(Player player, Integer day) {
        return playerProgressionCalendar.computeIfAbsent(player.getUniqueId(), _ -> new ArrayList<>())
                .contains(day);
    }

    private static void loadPlayerProgression() {
        try {
            playerProgressionCalendar.clear();
            adventCalendarProgressionDao.queryForAll().forEach(adventProgression ->
                    playerProgressionCalendar.put(
                            adventProgression.getPlayerUUID(),
                            Arrays.stream(adventProgression.getClaimed())
                                    .boxed()
                                    .collect(Collectors.toCollection(ArrayList::new))
                    ));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de chargement des Progressions des joueurs sur le calendrier de l'avent", e);
        }
    }

    private static void savePlayerProgression() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), AdventProgression.class);
            playerProgressionCalendar.forEach((uuid, claimed) ->
            {
                try {
                    adventCalendarProgressionDao.create(new AdventProgression(uuid, claimed.stream().mapToInt(Integer::intValue).toArray()));
                } catch (SQLException e) {
                    throw new RuntimeException("Erreur de sauvegarde des Progressions des joueurs sur le calendrier de l'avent", e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de sauvegarde des Progressions des joueurs sur le calendrier de l'avent", e);
        }
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new AdventCalendarCommands()
        );
    }
}

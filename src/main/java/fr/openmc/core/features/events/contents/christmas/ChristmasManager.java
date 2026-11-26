package fr.openmc.core.features.events.contents.christmas;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.DatabaseManager;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.christmas.listeners.DimensionListener;
import fr.openmc.core.features.events.contents.christmas.listeners.GingerbreadListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.advent.AdventCalendar;
import fr.openmc.core.features.events.contents.christmas.mecanism.advent.commands.AdventCalendarCommands;
import fr.openmc.core.features.events.contents.christmas.mecanism.advent.models.AdventProgression;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasDimensionManager;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.listeners.PlayerCommandListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.listeners.ReplaceBlockListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.GiftManager;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.commands.AdminGiftCommands;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.listeners.GiftBreakListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ChristmasManager extends Feature implements HasListeners, LoadAfterItemsAdder, HasDatabase, HasCommands {
    private final static Map<UUID, Set<Integer>> playerProgressionCalendar = new HashMap<>();
    private static Dao<AdventProgression, UUID> adventCalendarProgressionDao;

    @Override
    public void init() throws SQLException {
        ChristmasDimensionManager.init();
        OMCPlugin.registerFeature(new RiftManager());
        loadPlayerProgression();

        preventPlayersTask();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new AdminGiftCommands(),
                new AdventCalendarCommands()
        );
    }

    @Override
    public void save() {
        ChristmasDimensionManager.save();
        GiftManager.save();
        savePlayerProgression();
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                PlayerCommandListener::new,
                ReplaceBlockListener::new,
                GiftBreakListener::new,
                GingerbreadListener::new,
                DimensionListener::new
        );
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        GiftManager.initDB(connectionSource);

        TableUtils.createTableIfNotExists(connectionSource, AdventProgression.class);
        adventCalendarProgressionDao = DaoManager.createDao(connectionSource, AdventProgression.class);
    }

    private static void loadPlayerProgression() {
        try {
            playerProgressionCalendar.clear();
            adventCalendarProgressionDao.queryForAll().forEach(adventProgression ->
                    playerProgressionCalendar.put(
                            adventProgression.getPlayerUUID(),
                            Arrays.stream(adventProgression.getClaimed())
                                    .boxed()
                                    .collect(Collectors.toCollection(HashSet::new))
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

    public static void claimReward(Player player, Integer day) {
        playerProgressionCalendar.computeIfAbsent(player.getUniqueId(), _ -> new HashSet<>()).add(day);
    }

    public static boolean hasClaimReward(Player player, Integer day) {
        return playerProgressionCalendar.getOrDefault(player.getUniqueId(), Set.of()).contains(day);
    }

    public static boolean hasClaimAllCaseAvailable(Player player) {
        for (AdventCalendar adventCalendar : AdventCalendar.values()) {
            if (adventCalendar.isCaseOpened() && !hasClaimReward(player, adventCalendar.getDay()))
                return false;
        }
        return true;
    }

    private static void preventPlayersTask() {
        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            if (!AdventCalendar.isCalendarActive()) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (hasClaimAllCaseAvailable(player)) continue;

                MessagesManager.sendMessage(player,
                        TranslationManager.translation("feature.events.christmas.advent_calendar.case")
                                .clickEvent(ClickEvent.runCommand("/christmas calendar")),
                        Prefix.OPENMC, MessageType.INFO, false);
            }
        }, 20L * 60L, 20L * 60L * 30L);
    }
}

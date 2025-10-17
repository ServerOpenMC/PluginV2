package fr.openmc.core.features.homes;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.homes.command.*;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.models.HomeLimit;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
import fr.openmc.core.utils.database.DatabaseManager;
import lombok.Getter;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class HomesManager {

    public static final List<Home> homes = new ArrayList<>();
    public static final List<HomeLimit> homeLimits = new ArrayList<>();

    public HomesManager() {
        DisabledWorldHome.init();

        CommandsManager.getHandler().register(
                new SetHomeCommand(),
                new RenameHomeCommand(),
                new DelHomeCommand(),
                new RelocateHomeCommand(),
                new TpHomeCommand(),
                new HomeWorldCommand(),
                new UpgradeHomeCommand()
        );

        loadHomeLimit();
        loadHomes();
    }

    public static void saveHomesData() {
        saveHomes();
        saveHomeLimit();
    }

    public static void addHome(Home home) {
        homes.add(home);
    }

    public static void removeHome(Home home) {
        homes.remove(home);
    }

    public static void renameHome(Home home, String newName) {
        home.setName(newName);
    }

    public static void relocateHome(Home home, Location newLoc) {
        home.setLocation(newLoc);
    }

    public static List<Home> getHomes(UUID playerUUID) {
        return homes
                .stream()
                .filter(home -> home.getOwner().equals(playerUUID))
                .toList();
    }

    public static List<String> getHomesNames(UUID playerUUID) {
        return getHomes(playerUUID)
                .stream()
                .map(Home::getName)
                .toList();
    }

    public static int getHomeLimit(UUID playerUUID) {
        HomeLimit homeLimit = homeLimits.stream()
                .filter(hl -> hl.getPlayerUUID().equals(playerUUID))
                .findFirst()
                .orElse(null);

        if (homeLimit == null) {
            homeLimit = new HomeLimit(playerUUID, HomeLimits.LIMIT_0);
            homeLimits.add(homeLimit);
        }

        return homeLimit.getLimit();
    }

    public static void updateHomeLimit(UUID playerUUID) {
        HomeLimit homeLimit = homeLimits.stream()
                .filter(hl -> hl.getPlayerUUID().equals(playerUUID))
                .findFirst()
                .orElse(null);
        if (homeLimit == null) {
            homeLimits.add(new HomeLimit(playerUUID, HomeLimits.LIMIT_0));
        } else {
            int currentLimitIndex = homeLimit.getHomeLimit().ordinal();
            HomeLimits newLimit = HomeLimits.values()[currentLimitIndex + 1];
            homeLimit.setLimit(newLimit.getLimit());
        }
    }

    // DB methods

    private static Dao<Home, UUID> homesDao;
    private static Dao<HomeLimit, UUID> limitsDao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Home.class);
        homesDao = DaoManager.createDao(connectionSource, Home.class);

        TableUtils.createTableIfNotExists(connectionSource, HomeLimit.class);
        limitsDao = DaoManager.createDao(connectionSource, HomeLimit.class);
    }

    private static void loadHomeLimit() {
        try {
            homeLimits.addAll(limitsDao.queryForAll());

            for (HomeLimit homeLimit : homeLimits) {
                if (homeLimit.getLimit() == 0) homeLimit.setLimit(HomeLimits.LIMIT_0.getLimit());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de chargement des HomesLimit ", e);
        }
    }

    private static void saveHomeLimit() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), HomeLimit.class);
            limitsDao.create(homeLimits);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de sauvegarde des HomesLimit ", e);
        }
    }

    private static void loadHomes() {
        try {
            homes.addAll(homesDao.queryForAll());
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de chargement des Homes ", e);
        }
    }

    private static void saveHomes() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), Home.class);
            for (Home home : homes) {
                homesDao.createOrUpdate(home);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de sauvegarde des Homes ", e);
        }
    }
}

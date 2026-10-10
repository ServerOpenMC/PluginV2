package fr.openmc.core.features.homes;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.command.*;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.models.HomeLimit;
import fr.openmc.core.lifecycle.integration.DatabaseManager;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.lifecycle.interfaces.HasRegistries;
import fr.openmc.core.lifecycle.registries.LifecycleRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import lombok.Getter;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

@Credit(developers = {"Axeno"}, graphist = {"Gexary"})
@Getter
public class HomesManager extends Feature implements HasDatabase, HasCommands, HasRegistries {
    @Getter
    private final List<Home> homes = new ArrayList<>();
    private final List<HomeLimit> homeLimits = new ArrayList<>();

    @Override
    public void onEnable() {
        loadHomeLimit();
        loadHomes();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new SetHomeCommand(),
                new RenameHomeCommand(),
                new DelHomeCommand(),
                new RelocateHomeCommand(),
                new TpHomeCommand(),
                new HomeWorldCommand(),
                new UpgradeHomeCommand()
        );
    }

    @Override
    public void onDisable() {
        saveHomes();
        saveHomeLimit();
    }

    @Override
    public List<Supplier<LifecycleRegistry>> getRegistries() {
        return List.of(
                () -> SubRegistry.boot(new HomeFeaturesRegistry(),
                        r -> OMCRegistry.HOME_FEATURES = r)
        );
    }

    // DB methods

    private Dao<Home, UUID> homesDao;
    private Dao<HomeLimit, UUID> limitsDao;

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Home.class);
        homesDao = DaoManager.createDao(connectionSource, Home.class);

        TableUtils.createTableIfNotExists(connectionSource, HomeLimit.class);
        limitsDao = DaoManager.createDao(connectionSource, HomeLimit.class);
    }

    private void loadHomeLimit() {
        try {
            homeLimits.addAll(limitsDao.queryForAll());

            for (HomeLimit homeLimit : homeLimits) {
                if (homeLimit.getLimit() == 0) homeLimit.setLimit(HomeLimits.LIMIT_0.getLimit());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de chargement des HomesLimit ", e);
        }
    }

    private void saveHomeLimit() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), HomeLimit.class);
            limitsDao.create(homeLimits);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de sauvegarde des HomesLimit ", e);
        }
    }

    private void loadHomes() {
        try {
            homes.addAll(homesDao.queryForAll());
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de chargement des Homes ", e);
        }
    }

    private void saveHomes() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), Home.class);
            for (Home home : homes) {
                homesDao.createOrUpdate(home);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de sauvegarde des Homes ", e);
        }
    }

    public boolean addHome(Home home) {
        return homes.add(home);
    }

    public boolean removeHome(Home home) {
        return homes.remove(home);
    }

    public void renameHome(Home home, String newName) {
        home.setName(newName);
    }

    public void relocateHome(Home home, Location location) {
        home.setLocation(location);
    }

    public void addHomeLimit(HomeLimit homeLimit) {
        homeLimits.add(homeLimit);
    }
}

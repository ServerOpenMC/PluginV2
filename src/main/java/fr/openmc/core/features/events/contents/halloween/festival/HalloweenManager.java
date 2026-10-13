package fr.openmc.core.features.events.contents.halloween.festival;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.Floors;
import fr.openmc.core.features.events.contents.halloween.festival.listeners.CandyListener;
import fr.openmc.core.features.events.contents.halloween.festival.model.HalloweenDB;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class HalloweenManager extends Feature implements LoadAfterItemsAdder, HasDatabase, HasListeners, HasCommands {

    //TODO quand acheter truc dans shop, augmenté avancement bonbons pour débloqué les étages, quand tous les étages débloqué, faire obtenir des récompenses a tous les joueurs ayant contribué

    @Getter
    private static HalloweenDB halloweenDB;

    private static Dao<HalloweenDB, String> halloweenDao;

    public static List<String> unlockedFloors;

    private static File witchHouseSpawnFile;
    private static FileConfiguration witchHouseSpawnConfig;
    private static Location pasteLocation;

    @Override
    public void init() {
        witchHouseSpawnFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "witch_house_spawn.yml");
        loadWitchHouseSpawnConfig();

        halloweenDB = loadEvent();
        unlockedFloors = halloweenDB.getUnlockedRooms();
        WitchShopManager.init();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, HalloweenDB.class);
        halloweenDao = DaoManager.createDao(connectionSource, HalloweenDB.class);
    }

    @Override
    protected void save() {
        saveEvent();
    }

    public static HalloweenDB loadEvent() {
        try {
            HalloweenDB halloweenDB = halloweenDao.queryForFirst();
            if (halloweenDB == null)
                halloweenDB = new HalloweenDB(0, List.of(Floors.FLOOR_1.name()), false);
            return halloweenDB;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadWitchHouseSpawnConfig() {
        if(!witchHouseSpawnFile.exists()) {
            witchHouseSpawnFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/witch_house_spawn.yml", false);
        }

        witchHouseSpawnConfig = YamlConfiguration.loadConfiguration(witchHouseSpawnFile);
        loadPasteLocation();
    }

    private static void loadPasteLocation() {
        if (witchHouseSpawnConfig.contains("spawn")) {
            World world = OMCPlugin.getInstance().getServer().getWorld(witchHouseSpawnConfig.getString("spawn.world", "world"));
            double x = witchHouseSpawnConfig.getDouble("spawn.x", 0.0);
            double y = witchHouseSpawnConfig.getDouble("spawn.Y", 0.0);
            double z = witchHouseSpawnConfig.getDouble("spawn.z", 0.0);

            pasteLocation = new Location(
                    world,
                    x,
                    y,
                    z
            );
        }
    }

    private static void saveEvent() {
        try {
            halloweenDao.createOrUpdate(halloweenDB);
        } catch (Exception e) {
            OMCLogger.error("Impossible de sauvegarder l'event d'halloween pendant l'arret.", e);
        }
    }

    public static boolean hasUnlock(Floors floor) {
        return unlockedFloors.contains(floor.name());
    }

    public static boolean unlockNewRoom(Floors floor) {
        return unlockedFloors.add(floor.name());
    }

    public static void placeWitchShop() {
        OMCRegistry.CUSTOM_STRUCTURES.WITCH_SHOP.place(pasteLocation);
    }

    public static void removeWitchShop() {
        OMCRegistry.CUSTOM_STRUCTURES.WITCH_SHOP.removeAll();
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                CandyListener::new
        );
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(

        );
    }
}

package fr.openmc.core.features.city.sub.notation;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.city.sub.notation.commands.AdminNotationCommands;
import fr.openmc.core.features.city.sub.notation.commands.NotationCommands;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class NotationManager {

    public static final HashMap<String, List<CityNotation>> notationPerWeek = new HashMap<>();
    public static final HashMap<String, CityNotation> cityNotations = new HashMap<>();
    private static Dao<CityNotation, String> notationDao;

    public NotationManager() {
        loadNotations();
        CommandsManager.getHandler().register(
                new NotationCommands(),
                new AdminNotationCommands()
        );
    }

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CityNotation.class);
        notationDao = DaoManager.createDao(connectionSource, CityNotation.class);

    }

    public static void loadNotations() {
        try {
            List<CityNotation> notations = notationDao.queryForAll();

            notations.forEach(notation -> {
                String cityUUID = notation.getCityUUID();
                if (!cityNotations.containsKey(cityUUID)) {
                    cityNotations.put(cityUUID, notation);
                }

                String weekStr = notation.getWeekStr();
                notationPerWeek.computeIfAbsent(weekStr, k -> new java.util.ArrayList<>()).add(notation);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveNotations() {
        notationPerWeek.forEach((weekStr, notations) -> notations.forEach(notation -> {
                    try {
                        notationDao.createOrUpdate(notation);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                })
        );
    }

}

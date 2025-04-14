package fr.openmc.core.features.analytics;

import fr.openmc.core.OMCPlugin;
import java.sql.Connection;
import java.sql.SQLException;

public class AnalyticsManager {
    public static boolean isEnabled() {
        return OMCPlugin.getConfigs().getBoolean("features.analytics", false);
    }

    public static void init_db(Connection conn) throws SQLException {
        try {
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS stats (player VARCHAR(36) NOT NULL PRIMARY KEY , scope VARCHAR(255) NOT NULL , `value` BIGINT DEFAULT 0);").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

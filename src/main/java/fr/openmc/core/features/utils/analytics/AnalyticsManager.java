package fr.openmc.core.features.utils.analytics;

import fr.openmc.core.OMCPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public class AnalyticsManager {
    public static boolean isEnabled() {
        return OMCPlugin.getConfigs().getBoolean("analytics.enabled", false);
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS stats (player VARCHAR(36) NOT NULL PRIMARY KEY , scope VARCHAR(255) NOT NULL , value BIGINT DEFAULT 0);").executeUpdate();
    }
}
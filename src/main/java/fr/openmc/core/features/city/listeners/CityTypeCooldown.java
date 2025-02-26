package fr.openmc.core.features.city.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CityTypeCooldown implements Listener {

    private static final long COOLDOWN_TIME = 5 * 24 * 60 * 60 * 1000L; // 5 jours en ms

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS cooldowns (city_uuid VARCHAR(36) PRIMARY KEY, last_used BIGINT);").executeUpdate();
    }

    @EventHandler
    void onJoin (PlayerJoinEvent e){
        Player player = e.getPlayer();
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city!=null){
            String city_uuid = city.getUUID();
            if (isOnCooldown(city_uuid)){
                MessagesManager.sendMessage(player, Component.text("Type de ville changeable dans : " + getRemainingCooldown(city_uuid)/1000 + "s"), Prefix.CITY, MessageType.INFO, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Type de ville changeable"), Prefix.CITY, MessageType.INFO, false);
            }
        }
    }

    public static boolean isOnCooldown(String city_uuid) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT last_used FROM cooldowns WHERE city_uuid = ?")) {

            ps.setString(1, city_uuid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long lastUsed = rs.getLong("last_used");
                long currentTime = System.currentTimeMillis();
                return (currentTime - lastUsed) < COOLDOWN_TIME;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setCooldown(String city_uuid) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("REPLACE INTO cooldowns (city_uuid, last_used) VALUES (?, ?)")) {

            ps.setString(1, city_uuid);
            ps.setLong(2, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static long getRemainingCooldown(String city_uuid) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT last_used FROM cooldowns WHERE city_uuid = ?")) {

            ps.setString(1, city_uuid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long lastUsed = rs.getLong("last_used");
                long timeElapsed = System.currentTimeMillis() - lastUsed;
                return COOLDOWN_TIME - timeElapsed;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void removeCityCooldown(String city_uuid) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM cooldowns WHERE city_uuid = ?");
            statement.setString(1, city_uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}


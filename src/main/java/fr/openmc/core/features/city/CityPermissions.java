package fr.openmc.core.features.city;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CityPermissions {
    private static HashMap<String, HashMap<UUID, Set<CPermission>>> permsCache = new HashMap<>();
    /*
    permsCache
    - String: city name
        - UUID: player UUID, Can be empty
            - CPermission: permission
            - CPermission: permission
     */

    /**
     * /!\ Does not load permissions
     */
    public static UUID getPlayerWith(String city, CPermission permission) {
        HashMap<UUID, Set<CPermission>> cityPerms = permsCache.get(city);

        if (cityPerms == null) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT player FROM city_permissions WHERE city_uuid = ? AND permission = ?");
                statement.setString(1, city);
                statement.setString(2, permission.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.getFetchSize() == 0) {
                    return null;
                }
                resultSet.next();
                return UUID.fromString(resultSet.getString(0));
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            for (UUID player : cityPerms.keySet()) {
                if (cityPerms.get(player).contains(permission)) {
                    return player;
                }
            }
        }
        return null;
    }

    public static boolean hasPermission(String city, UUID uuid, CPermission permission) {
        HashMap<UUID, Set<CPermission>> cityPerms = permsCache.getOrDefault(city, new HashMap<>());

        if (!cityPerms.containsKey(uuid)) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_permissions WHERE city_uuid = ? AND player = ?");
                statement.setString(1, city);
                statement.setString(2, uuid.toString());
                ResultSet rs = statement.executeQuery();

                Set<CPermission> plrPerms = cityPerms.getOrDefault(uuid, new HashSet<>());

                while (rs.next()) {
                    try {
                        plrPerms.add(CPermission.valueOf(rs.getString("permission")));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid permission: " + rs.getString("permission"));
                    }
                }

                cityPerms.put(uuid, plrPerms);
                permsCache.put(city, cityPerms);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        Set<CPermission> playerPerms = cityPerms.getOrDefault(uuid, new HashSet<>()); // Player perms or no perms

        if (playerPerms.contains(CPermission.OWNER)) {
            return true;
        }

        return playerPerms.contains(permission);
    }

    public static boolean removePermission(String city, UUID uuid, CPermission permission) {
        HashMap<UUID, Set<CPermission>> cityPerms = permsCache.getOrDefault(city, new HashMap<>());
        Set<CPermission> playerPerms = cityPerms.getOrDefault(uuid, new HashSet<>());

        if (playerPerms.contains(permission)) {
            playerPerms.remove(permission);
            cityPerms.put(uuid, playerPerms);
            permsCache.put(city, cityPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_permissions WHERE city_uuid = ? AND player = ? AND permission = ?");
                    statement.setString(1, city);
                    statement.setString(2, uuid.toString());
                    statement.setString(3, permission.toString());
                    statement.executeUpdate();
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            return true;
        }
        return false;
    }

    public static void forgetPlayer(String city, UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_permissions WHERE city_uuid = ? AND player = ?");
                statement.setString(1, city);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
                permsCache.get(city).remove(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void forgetCity(String city) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_permissions WHERE city_uuid = ?");
                statement.setString(1, city);
                statement.executeUpdate();
                permsCache.remove(city);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void addPermission(String city, UUID uuid, CPermission permission) {
        HashMap<UUID, Set<CPermission>> cityPerms = permsCache.getOrDefault(city, new HashMap<>());
        Set<CPermission> playerPerms = cityPerms.getOrDefault(uuid, new HashSet<>());

        if (!playerPerms.contains(permission)) {
            playerPerms.add(permission);
            cityPerms.put(uuid, playerPerms);
            permsCache.put(city, cityPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_permissions (city_uuid, player, permission) VALUES (?, ?, ?)");
                    statement.setString(1, city);
                    statement.setString(2, uuid.toString());
                    statement.setString(3, permission.toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
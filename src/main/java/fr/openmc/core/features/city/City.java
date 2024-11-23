package fr.openmc.core.features.city;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class City {
    private final String city_uuid;
    private HashMap<UUID, Set<CPermission>> permsCache = new HashMap<>();
    private Set<UUID> members = new HashSet<>();
    private Double balance;
    private String name;

    public City(String uuid) {
        this.city_uuid = uuid;

        CityManager.registerCity(this);

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_permissions WHERE city_uuid = ?");
            statement.setString(1, city_uuid);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                try {
                    UUID player = UUID.fromString(rs.getString("player"));
                    CPermission permission = CPermission.valueOf(rs.getString("permission"));

                    Set<CPermission> playerPerms = permsCache.getOrDefault(player, new HashSet<>());
                    playerPerms.add(permission);
                    permsCache.put(player, playerPerms);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid permission: " + rs.getString("permission"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUUID() {
        return city_uuid;
    }

    public @NotNull String getName() {
        if (name != null) return name;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT name FROM city WHERE uuid = ?");
            statement.setString(1, city_uuid);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("name");
                return name;
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
        return "inconnu";
    }

    /**
     * Gets the list of members (UUIDs) of a specific city.
     *
     * @return A list of UUIDs representing the members of the city.
     */
    public Set<UUID> getMembers() {
        if (!members.isEmpty()) return members;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT player FROM city_members WHERE city_uuid = ?");
            statement.setString(1, city_uuid);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                members.add(UUID.fromString(resultSet.getString(1)));
                CityManager.cachePlayer(UUID.fromString(resultSet.getString(1)), this);
            }

            return members;
        } catch (SQLException err) {
            err.printStackTrace();
            return Set.of();
        }
    }

    /**
     * Sets the balance for a given City and updates it in the database asynchronously.
     *
     * @param value The new balance value to be set.
     */
    public void setBalance( Double value) {
        Double old = balance;
        balance = value;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET balance=? WHERE uuid=?;");
                statement.setDouble(1, value);
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                balance = old;
            }
        });
    }

    /**
     * Renames a city.
     *
     * @param newName The new name for the city.
     */
    public void renameCity(String newName) {
        name = newName;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET name=? WHERE uuid=?;");
                statement.setString(1, newName);
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Retrieves the balance for a given UUID. If the balance is not cached, it retrieves it from the database.
     *
     * @return The balance of the city, or 0 if no balance is found or an error occurs.
     */
    @NotNull
    public Double getBalance() {
        if (balance != null) return balance;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT balance FROM city WHERE uuid = ?");
            statement.setString(1, city_uuid);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("balance");
                return balance;
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }

        return 0d;
    }

    /**
     * Changes the owner of a city.
     *
     * @param player The UUID of the new owner.
     */
    public void changeOwner(UUID player) {
        removePermission(getPlayerWith(CPermission.OWNER), CPermission.OWNER);
        addPermission(player, CPermission.OWNER);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET owner=? WHERE uuid=?;");
                statement.setString(1, player.toString());
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the balance for a given City by adding a difference amount and updating it in the database asynchronously.
     *
     * @param diff The amount to be added to the existing balance.
     */
    public void updateBalance(Double diff) {
        Double old = balance;
        balance += diff;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET balance=balance+? WHERE uuid=?;");
                statement.setDouble(1, diff);
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                balance = old;
            }
        });
    }

    /**
     * Retrieves the name of a city by its UUID.
     *
     * @return The name of the city, or null if the city does not exist.
     */
    @Nullable
    public String getCityName() {
        if (name != null) return name;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT name FROM city WHERE uuid = ? LIMIT 1");
            statement.setString(1, city_uuid);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("name");
                return name;
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
        return "inconnu";
    }

    public boolean hasPermission(UUID uuid, CPermission permission) {
        if (!permsCache.containsKey(uuid)) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_permissions WHERE city_uuid = ? AND player = ?");
                statement.setString(1, city_uuid);
                statement.setString(2, uuid.toString());
                ResultSet rs = statement.executeQuery();

                Set<CPermission> plrPerms = permsCache.getOrDefault(uuid, new HashSet<>());

                while (rs.next()) {
                    try {
                        plrPerms.add(CPermission.valueOf(rs.getString("permission")));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid permission: " + rs.getString("permission"));
                    }
                }

                permsCache.put(uuid, plrPerms);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        Set<CPermission> playerPerms = permsCache.get(uuid);

        if (playerPerms.contains(CPermission.OWNER)) {
            return true;
        }

        return playerPerms.contains(permission);
    }

    public UUID getPlayerWith(CPermission permission) {
        for (UUID player: permsCache.keySet()) {
            if (permsCache.get(player).contains(permission)) {
                return player;
            }
        }
        return null;
    }

    public boolean removePermission(UUID uuid, CPermission permission) {
        Set<CPermission> playerPerms = permsCache.get(uuid);

        if (playerPerms == null) {
            return true;
        }

        if (playerPerms.contains(permission)) {
            playerPerms.remove(permission);
            permsCache.put(uuid, playerPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_permissions WHERE city_uuid = ? AND player = ? AND permission = ?");
                    statement.setString(1, city_uuid);
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

    /**
     * Delete every information about a player
     * @param uuid Player to forgot
     */
    public void forgetPlayer(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_permissions WHERE city_uuid = ? AND player = ?");
                statement.setString(1, city_uuid);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
                permsCache.remove(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addPermission(UUID uuid, CPermission permission) {
        Set<CPermission> playerPerms = permsCache.getOrDefault(uuid, new HashSet<>());

        if (!playerPerms.contains(permission)) {
            playerPerms.add(permission);
            permsCache.put(uuid, playerPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_permissions (city_uuid, player, permission) VALUES (?, ?, ?)");
                    statement.setString(1, city_uuid);
                    statement.setString(2, uuid.toString());
                    statement.setString(3, permission.toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Allows a player to leave a city and updates the database and region permissions.
     *
     * @param player The UUID of the player leaving the city.
     * @return True if the player successfully left the city, false otherwise.
     */
    public boolean removePlayer(UUID player) {
        forgetPlayer(player);
        CityManager.uncachePlayer(player);
        members.remove(player);

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_members WHERE player=?");
            statement.setString(1, player.toString());
            statement.executeUpdate();

            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
            regionManager.getRegion("city_"+ city_uuid).getMembers().removePlayer(player);

            regionManager.saveChanges();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Adds a player as a member of a specific city.
     *
     * @param player The UUID of the player to add.
     */
    public void addPlayer(UUID player) {
        members.add(player);
        CityManager.cachePlayer(player, this);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
                regionManager.getRegion("city_"+city_uuid).getMembers().addPlayer(player);
                regionManager.saveChanges();

                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_members VALUE (?, ?)");
                statement.setString(1, city_uuid);
                statement.setString(2, player.toString());
                statement.executeUpdate();
            } catch (SQLException | StorageException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Deletes a city, removing it from records and updating members and regions accordingly.
     *
     * @return Is a success
     */
    public void delete() {
        CityManager.forgetCity(city_uuid);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
                assert regionManager != null;
                regionManager.removeRegion("city_"+city_uuid);
                regionManager.saveChanges();

                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_members WHERE city_uuid=?");
                statement.setString(1, city_uuid);
                statement.executeUpdate();

                statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city WHERE uuid=?");
                statement.setString(1, city_uuid);
                statement.executeUpdate();

                statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_permissions WHERE city_uuid=?");
                statement.setString(1, city_uuid);
                statement.executeUpdate();
            } catch (SQLException | StorageException e) {
                e.printStackTrace();
            }
        });
    }
}

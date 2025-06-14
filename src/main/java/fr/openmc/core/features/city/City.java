package fr.openmc.core.features.city;

import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.events.*;
import fr.openmc.core.features.city.mascots.Mascot;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.city.mayor.CityLaw;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.NPCManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.openmc.core.features.city.mayor.managers.MayorManager.*;

public class City {
    private String name;
    private final String cityUUID;
    private Set<UUID> members = new HashSet<>();
    private Double balance = Double.valueOf(0); // set default value cause if its null, error in updateBalance
    private Set<BlockVector2> chunks = new HashSet<>(); // Liste des chunks claims par la ville
    private HashMap<UUID, Set<CPermission>> permsCache = new HashMap<>();
    private List<CityRank> cityRanks = new ArrayList<>(); // Liste des grades de la ville
    private MayorManager mayorManager;
    private CityType cachedType;
    private Integer cachedPowerPoints;

    private Integer chestPages;
    private HashMap<Integer, ItemStack[]> chestContent = new HashMap<>();
    @Getter @Setter private UUID chestWatcher;

    public City(String uuid) {
        this.cityUUID = uuid;

        CityManager.registerCity(this);

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid, player, permission FROM city_permissions WHERE city_uuid = ?");
            statement.setString(1, cityUUID);
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

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT balance FROM city WHERE uuid = ?");
            statement.setString(1, cityUUID);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                try {
                    balance = rs.getDouble("balance");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid permission: " + rs.getString("permission"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        this.mayorManager = getInstance();
        
        loadRanks();
    }

    // ==================== Global Methods ====================

    /**
     * Retrieves the UUID of a city.
     *
     * @return The UUID of the city.
     */
    public String getUUID() {
        return cityUUID;
    }

    /**
     * Retrieves the name of a city by its UUID. If the name is not cached, it retrieves it from the database.
     *
     * @return The name of the city, or "inconnu" if the city does not exist or an error occurs.
     */
    public @NotNull String getName() {
        if (name != null) return name;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT name FROM city WHERE uuid = ? LIMIT 1");
            statement.setString(1, cityUUID);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("name");
                return name;
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
        return "Inconnu";
    }

    /**
     * Renames a city.
     *
     * @param newName The new name for the city.
     */
    public void renameCity(String newName) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityRenameEvent(this.name, this));
        });
        name = newName;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET name=? WHERE uuid=?;");
                statement.setString(1, newName);
                statement.setString(2, cityUUID);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Retrieves the type of the city (CityType.WAR or CityType.PEACE).
     *
     * @return The type of the city, or null if not found.
     */
    public CityType getType() {
        if (cachedType != null) return cachedType;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT type FROM city WHERE uuid = ?");
            statement.setString(1, cityUUID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String typeString = rs.getString("type");
                if ("war".equalsIgnoreCase(typeString)) {
                    cachedType = CityType.WAR;
                } else if ("peace".equalsIgnoreCase(typeString)) {
                    cachedType = CityType.PEACE;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return cachedType;
    }

    /**
     * Changes the type of the city (from war to peace or vice versa) and updates it in the database asynchronously.
     */
    public void changeType() {
        CityType cityType = getType();
        String cityTypeString = "";
        if (cityType != null) {

            if (cityType.equals(CityType.WAR)) {
                cityTypeString = "peace";
                cachedType = CityType.PEACE;
            } else if (cityType.equals(CityType.PEACE)) {
                cityTypeString = "war";
                cachedType = CityType.WAR;
            }
        }

        String finalCityType = cityTypeString;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET type=? WHERE uuid=?;");
                statement.setString(1, finalCityType);
                statement.setString(2, cityUUID);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Deletes a city, removing it from records and updating members and regions accordingly.
     */
    public void delete() {
        CityManager.forgetCity(cityUUID);

        NPCManager.removeNPCS(cityUUID);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                String[] queries = {
                        "DELETE FROM city_members WHERE city_uuid=?",
                        "DELETE FROM city WHERE uuid=?",
                        "DELETE FROM city_permissions WHERE city_uuid=?",
                        "DELETE FROM city_regions WHERE city_uuid=?",
                        "DELETE FROM city_chests WHERE city_uuid=?",
                        "DELETE FROM city_power WHERE city_uuid=?",
                        "DELETE FROM " + TABLE_MAYOR + " WHERE city_uuid = ?",
                        "DELETE FROM " + TABLE_ELECTION + " WHERE city_uuid = ?",
                        "DELETE FROM " + TABLE_VOTE + " WHERE city_uuid = ?"
                };

                for (String sql : queries) {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql);
                    statement.setString(1, cityUUID);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityDeleteEvent(this));
        });
    }

    // ==================== Members Methods ====================

    /**
     * Gets the list of members (UUIDs) of a specific city.
     *
     * @return A list of UUIDs representing the members of the city.
     */
    public Set<UUID> getMembers() {
        if (!members.isEmpty()) return members;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT player FROM city_members WHERE city_uuid = ?");
            statement.setString(1, cityUUID);

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
     * Checks if a player is a member of the city.
     *
     * @param player The player to check.
     * @return True if the player is a member, false otherwise.
     */
    public boolean isMember(Player player) {
        return this.getMembers().contains(player.getUniqueId());
    }

    /**
     * Adds a player as a member of a specific city.
     *
     * @param player The UUID of the player to add.
     */
    public void addPlayer(UUID player) {
        members.add(player);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new MemberJoinEvent(CacheOfflinePlayer.getOfflinePlayer(player), this));
        });
        CityManager.cachePlayer(player, this);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_members VALUE (?, ?)");
                statement.setString(1, cityUUID);
                statement.setString(2, player.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new MemberLeaveEvent(CacheOfflinePlayer.getOfflinePlayer(player), this));
        });
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_members WHERE player=?");
            statement.setString(1, player.toString());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
                statement.setString(2, cityUUID);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Delete every information about a player
     *
     * @param uuid Player to forgot
     */
    public void forgetPlayer(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_permissions WHERE city_uuid = ? AND player = ?");
                statement.setString(1, cityUUID);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
                permsCache.remove(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // ==================== Chest Methods ====================

    /**
     * Gets the content of a specific chest page for a city.
     *
     * @param page The page number of the chest.
     * @return The content of the chest page as an array of ItemStack.
     */
    public ItemStack[] getChestContent(int page) {
        if (chestContent.containsKey(page)) {
            return chestContent.get(page);
        }

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT content FROM city_chests WHERE city_uuid = ? AND page = ? LIMIT 1");
            statement.setString(1, cityUUID);
            statement.setInt(2, page);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                byte[] content = rs.getBytes("content");
                if (content == null) {
                    return new ItemStack[54];
                }
                chestContent.put(page, ItemStack.deserializeItemsFromBytes(content));
                return chestContent.get(page);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // On ne peut pas retourner une liste vide, car s'il ferme, ça va reset son inv
            throw new RuntimeException("Error while loading chest content");
        }
        return new ItemStack[54]; // ayayay
    }

    /**
     * Saves the content of a specific chest page for a city.
     * @param page The page number of the chest.
     * @param content The content to save as an array of ItemStack.
     */
    public void saveChestContent(int page, ItemStack[] content) {
        chestContent.put(page, content);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city_chests SET content=? WHERE city_uuid=? AND page=?");
                statement.setBytes(1, ItemStack.serializeItemsAsBytes(content));
                statement.setString(2, cityUUID);
                statement.setInt(3, page);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Retrieves the number of pages for a city's chest.
     *
     * @return The number of pages for the city's chest.
     */
    public @NotNull Integer getChestPages() {
        if (chestPages != null) return chestPages;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT COUNT(page) FROM city_chests WHERE city_uuid = ?");
            statement.setString(1, cityUUID);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                chestPages = resultSet.getInt(1);
                return chestPages;
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
        return 0;
    }

    /**
     * Upgrades the city's chest by adding a new page and updating the database asynchronously.
     */
    public void upgradeChest() {
        chestPages += 1;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_chests (city_uuid, page) VALUES (?, ?)");
                statement.setString(1, cityUUID);
                statement.setInt(2, chestPages);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                chestPages -= 1;
            }
        });
    }

    // ==================== Chunk Methods ====================

    /**
     * Adds a chunk to the city's claimed chunks and updates the database asynchronously.
     *
     * @param chunk The chunk to be added.
     */
    public void addChunk(Chunk chunk) {
        getChunks(); // Load chunks

        if (chunks.contains(BlockVector2.at(chunk.getX(), chunk.getZ()))) return;
        chunks.add(BlockVector2.at(chunk.getX(), chunk.getZ()));

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_regions (city_uuid, x, z) VALUES (?, ?, ?)");
                statement.setString(1, cityUUID);
                statement.setInt(2, chunk.getX());
                statement.setInt(3, chunk.getZ());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new ChunkClaimedEvent(this, chunk));
        });
    }

    /**
     * Removes a chunk from the city's claimed chunks and updates the database asynchronously.
     *
     * @param chunkX The X coordinate of the chunk to be removed.
     * @param chunkZ The Z coordinate of the chunk to be removed.
     * @return True if the chunk was successfully removed, false otherwise.
     */
    public boolean removeChunk(int chunkX, int chunkZ) {
        getChunks(); // Load chunks

        if (!chunks.contains(BlockVector2.at(chunkX, chunkZ))) return false;
        chunks.remove(BlockVector2.at(chunkX, chunkZ));

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_regions (city_uuid, x, z) VALUES (?, ?, ?)");
                statement.setString(1, cityUUID);
                statement.setInt(2, chunkX);
                statement.setInt(3, chunkZ);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    /**
     * Retrieves the set of chunks claimed by the city.
     *
     * @return A set of BlockVector2 representing the claimed chunks.
     */
    public @NotNull Set<BlockVector2> getChunks() {
        if (!chunks.isEmpty()) return chunks;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT x, z FROM city_regions WHERE city_uuid = ?");
            statement.setString(1, cityUUID);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                chunks.add(BlockVector2.at(resultSet.getInt("x"), resultSet.getInt("z")));
            }

            return chunks;
        } catch (SQLException err) {
            err.printStackTrace();
            return Set.of();
        }
    }

    /**
     * Checks if a specific chunk is claimed by the city.
     *
     * @param chunkX The X coordinate of the chunk to check.
     * @param chunkZ The Z coordinate of the chunk to check.
     * @return True if the chunk is claimed, false otherwise.
     */
    public boolean hasChunk(int chunkX, int chunkZ) {
        getChunks(); // Load chunks
        return chunks.contains(BlockVector2.at(chunkX, chunkZ));
    }

    // ==================== Power Points Methods ====================

    /**
     * Retrieves the power points of the city.
     *
     * @return The power points of the city, or 0 if not found.
     */
    public int getPowerPoints() {
        if (cachedPowerPoints != null) return cachedPowerPoints;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT points FROM city_power WHERE city_uuid = ?");
            statement.setString(1, cityUUID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                cachedPowerPoints = rs.getInt("points");
            } else {
                cachedPowerPoints = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            cachedPowerPoints = 0;
        }

        return cachedPowerPoints;
    }

    /**
     * Updates the power of a City by adding or removing points.
     *
     * @param point The amount to be added or remove to the existing power.
     */
    public void updatePowerPoints(int point){
        try {
            int result = getPowerPoints() + point;
            if (result < 0) result = 0;
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city_power SET power_point=? WHERE city_uuid=?;");
            statement.setInt(1, result);
            cachedPowerPoints = result;
            statement.setString(2, cityUUID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== Mayor Methods ====================

    /**
     * Retrieves the mayor of the city.
     *
     * @return The mayor of the city, or null if not found.
     */
    public Mayor getMayor() {
        MayorManager mayorManager = getInstance();

        return mayorManager.cityMayor.get(CityManager.getCity(cityUUID));
    }

    /**
     * Checks if the city has a mayor.
     *
     * @return True if the city has a mayor, false otherwise.
     */
    public boolean hasMayor() {
        Mayor mayor = mayorManager.cityMayor.get(this);
        if (mayor == null) return false;

        return mayor.getUUID() != null;
    }

    /**
     * Retrieves the election type of the city.
     *
     * @return The election type of the city, or null if not found.
     */
    public ElectionType getElectionType() {
        Mayor mayor = mayorManager.cityMayor.get(this);
        if (mayor == null) return null;

        return mayor.getElectionType();
    }

    /**
     * Retrieves the law of the city.
     *
     * @return The law of the city, or null if not found.
     */
    public CityLaw getLaw() {
        MayorManager mayorManager = getInstance();

        return mayorManager.cityLaws.get(CityManager.getCity(cityUUID));
    }

    // ==================== Economy Methods ====================

    /**
     * Sets the balance for a given City and updates it in the database asynchronously.
     *
     * @param value The new balance value to be set.
     */
    public void setBalance(Double value) {
        Double old = getBalance();
        balance = value;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET balance=? WHERE uuid=?;");
                statement.setDouble(1, value);
                statement.setString(2, cityUUID);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                balance = old;
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
            statement.setString(1, cityUUID);

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
     * Updates the balance for a given City by adding a difference amount and updating it in the database asynchronously.
     *
     * @param diff The amount to be added to the existing balance.
     */
    public void updateBalance(Double diff) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    Bukkit.getPluginManager().callEvent(new CityMoneyUpdateEvent(this, balance, balance + diff));
                });
        setBalance(balance+diff);
    }

    /**
     * Adds money to the city bank and removes it from {@link Player}
     * @param player The player depositing into the bank
     * @param input The input string to get the money value
     */
    public void depositCityBank(Player player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), moneyDeposit)) {
                updateBalance(moneyDeposit);
                MessagesManager.sendMessage(player, Component.text("Tu as transféré §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
        }
    }

    /**
     * Removes money from the city bank and add it to {@link Player}
     * @param player The player withdrawing from the bank
     * @param input The input string to get the money value
     */
    public void withdrawCityBank(Player player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (getBalance() < moneyDeposit) {
                MessagesManager.sendMessage(player, Component.text("Ta ville n'a pas assez d'argent en banque"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                updateBalance(moneyDeposit * -1);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), moneyDeposit);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
        }
    }

    /**
     * Calculates the interest for the city
     * Interests calculated as proportion not percentage (eg: 0.01 = 1%)
     *
     * @return The calculated interest as a double.
     */
    public double calculateCityInterest() {
        double interest = .01; // base interest is 1%
        
        if (getInstance().phaseMayor == 2) {
            if (PerkManager.hasPerk(getMayor(), Perks.BUISNESS_MAN.getId())) {
                interest = .03; // interest is 3% when perk Buisness Man actived
            }
        }

        return interest;
    }

    /**
     * Applies the interest to the city balance and updates it in the database.
     */
    public void applyCityInterest() {
        double interest = calculateCityInterest();
        double amount = getBalance() * interest;
        updateBalance(amount);
    }

    // ==================== Permissions Methods ====================

    /**
     * Retrieves the player with a specific permission.
     *
     * @param permission The permission to check for.
     * @return The UUID of the player with the permission, or null if not found.
     */
    public UUID getPlayerWith(CPermission permission) {
        for (UUID player : permsCache.keySet()) {
            if (permsCache.get(player).contains(permission)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Loads the permissions for a specific player from the database.
     *
     * @param player The UUID of the player to load permissions for.
     * @return True if permissions were successfully loaded, false otherwise.
     */
    private boolean loadPermission(UUID player) {
        if (!permsCache.containsKey(player)) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid, player, permission FROM city_permissions WHERE city_uuid = ? AND player = ?");
                statement.setString(1, cityUUID);
                statement.setString(2, player.toString());
                ResultSet rs = statement.executeQuery();

                Set<CPermission> plrPerms = permsCache.getOrDefault(player, new HashSet<>());

                while (rs.next()) {
                    try {
                        plrPerms.add(CPermission.valueOf(rs.getString("permission")));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid permission: " + rs.getString("permission"));
                    }
                }

                permsCache.put(player, plrPerms);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the permissions for a specific player.
     *
     * @param player The UUID of the player to retrieve permissions for.
     * @return A set of permissions for the player.
     */
    public Set<CPermission> getPermissions(UUID player) {
        loadPermission(player);
        return permsCache.get(player);
    }

    /**
     * Checks if a player has a specific permission.
     *
     * @param uuid The UUID of the player to check.
     * @param permission The permission to check for.
     * @return True if the player has the permission, false otherwise.
     */
    public boolean hasPermission(UUID uuid, CPermission permission) {
        loadPermission(uuid);
        Set<CPermission> playerPerms = permsCache.get(uuid);

        if (playerPerms.contains(CPermission.OWNER)) return true;

        return playerPerms.contains(permission);
    }

    /**
     * Adds a specific permission to a player and updates the database asynchronously.
     *
     * @param uuid       The UUID of the player to add the permission to.
     * @param permission The permission to add.
     */
    public void addPermission(UUID uuid, CPermission permission) {
        Set<CPermission> playerPerms = permsCache.getOrDefault(uuid, new HashSet<>());

        if (!playerPerms.contains(permission)) {
            playerPerms.add(permission);
            permsCache.put(uuid, playerPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_permissions (city_uuid, player, permission) VALUES (?, ?, ?)");
                    statement.setString(1, cityUUID);
                    statement.setString(2, uuid.toString());
                    statement.setString(3, permission.toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new CityPermissionChangeEvent(this, CacheOfflinePlayer.getOfflinePlayer(uuid), permission, true));
            });
        }
    }

    /**
     * Removes a specific permission from a player and updates the database asynchronously.
     *
     * @param uuid The UUID of the player to remove the permission from.
     * @param permission The permission to remove.
     * @return True if the permission was successfully removed, false otherwise.
     */
    public boolean removePermission(UUID uuid, CPermission permission) {
        loadPermission(uuid);
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
                    statement.setString(1, cityUUID);
                    statement.setString(2, uuid.toString());
                    statement.setString(3, permission.toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new CityPermissionChangeEvent(this, CacheOfflinePlayer.getOfflinePlayer(uuid), permission, false));
            });
            return true;
        }
        return false;
    }

    // ==================== Mascots Methods ====================

    public Mascot getMascot() {
        for (Mascot mascot : MascotsManager.mascots) {
            if (mascot.getCityUUID().equals(cityUUID)) {
                return mascot;
            }
        }
        return null;
    }
    
    public List<CityRank> getRanks() {
        return cityRanks;
    }
    
    public boolean isRanksFull() {
        return cityRanks.size() >= 18;
    }
    
    public CityRank getRankByName(String rankName) {
        for (CityRank rank : cityRanks) {
            if (rank.getName().equalsIgnoreCase(rankName)) {
                return rank;
            }
        }
        return null;
    }
    
    public boolean isRankExists(CityRank rank) {
        return cityRanks.contains(rank);
    }
    
    public boolean isRankExists(String rankName) {
        for (CityRank rank : cityRanks) {
            if (rank.getName().equalsIgnoreCase(rankName)) {
                return true;
            }
        }
        return false;
    }
    
    public void createRank(CityRank rank) {
        if (cityRanks.size() >= 18) {
            throw new IllegalStateException("Cannot add more than 18 ranks to a city.");
        }
        cityRanks.add(rank);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_ranks (city_uuid, name, permissions, priority, icon) VALUES (?, ?, ?, ?, ?)")) {
                statement.setString(1, cityUUID);
                statement.setString(2, rank.getName());
                statement.setString(3, rank.getPermissions().stream().map(Enum::name).collect(Collectors.joining(",")));
                statement.setInt(4, rank.getPriority());
                statement.setString(5, rank.getIcon() != null ? rank.getIcon().name() : Material.GOLD_BLOCK.name());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void deleteRank(CityRank rank) {
        if (! cityRanks.contains(rank)) {
            throw new IllegalArgumentException("Rank not found in the city's ranks.");
        }
        if (rank.getPriority() == 0) {
            throw new IllegalArgumentException("Cannot delete the default rank (priority 0).");
        }
        cityRanks.remove(rank);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_ranks WHERE city_uuid = ? AND name = ?")) {
                statement.setString(1, cityUUID);
                statement.setString(2, rank.getName());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void loadRanks() {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT name, permissions, priority, icon FROM city_ranks WHERE city_uuid = ?")) {
            statement.setString(1, cityUUID);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("name");
                Set<CPermission> permissions = Arrays.stream(rs.getString("permissions").split(","))
                        .map(CPermission::valueOf)
                        .collect(Collectors.toSet());
                byte priority = rs.getByte("priority");
                Material icon = rs.getString("icon") != null ? Material.valueOf(rs.getString("icon")) : Material.GOLD_BLOCK;
                
                CityRank rank = new CityRank(name, priority, permissions, icon);
                cityRanks.add(rank);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateRank(CityRank oldRank, CityRank newRank) {
        if (cityRanks.contains(oldRank)) {
            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city_ranks SET name = ?, permissions = ?, priority = ?, icon = ? WHERE city_uuid = ? AND name = ?")) {
                    statement.setString(1, newRank.getName());
                    statement.setString(2, newRank.getPermissions().stream().map(Enum::name).collect(Collectors.joining(",")));
                    statement.setInt(3, newRank.getPriority());
                    statement.setString(4, newRank.getIcon() != null ? newRank.getIcon().name() : Material.GOLD_BLOCK.name());
                    statement.setString(5, cityUUID);
                    statement.setString(6, oldRank.getName());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            cityRanks.remove(oldRank);
            cityRanks.add(newRank);
        } else {
            throw new IllegalArgumentException("Old rank not found in the city's ranks.");
        }
    }
}

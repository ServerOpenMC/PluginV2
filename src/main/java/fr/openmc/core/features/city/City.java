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
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.city.models.DBCity;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class City {
    @Getter
    private String name;
    private final String cityUUID;
    @Getter
    private final Set<UUID> members;
    @Getter
    private final Set<BlockVector2> chunks; // Liste des chunks claims par la ville
    private final HashMap<UUID, Set<CPermission>> permissions;
    private final HashMap<Integer, ItemStack[]> chestContent;
    private int chestPages;
    @Getter
    private double balance;
    @Getter
    private CityType type;
    @Getter
    private int powerPoints;
    @Getter
    private int freeClaims;

    @Getter
    @Setter
    private UUID chestWatcher;

    /**
     * Constructor used for City creation
     */
    public City(String id, String name, UUID owner, CityType type) {
        this.cityUUID = id;
        this.name = name;
        this.type = type;
        this.freeClaims = 15;

        CityManager.saveCity(this);
        CityManager.registerCity(this);

        this.members = new HashSet<>();
        this.permissions = new HashMap<>();
        this.chunks = new HashSet<>();
        this.chestContent = new HashMap<>();

        addPlayer(owner);
        addPermission(owner, CPermission.OWNER);
        saveChestContent(1, null);
    }

    /**
     * Constructor used to deserialize City database object
     */
    public City(String id, String name, double balance, String type, int power, int freeClaims) {
        this.cityUUID = id;
        this.name = name;
        this.balance = balance;
        this.freeClaims = freeClaims;
        this.powerPoints = power;

        setType(type);

        CityManager.registerCity(this);

        this.members = CityManager.getCityMembers(this);
        this.permissions = CityManager.getCityPermissions(this);
        this.chunks = CityManager.getCityChunks(this);
        this.chestContent = CityManager.getCityChestContent(this);
        this.chestPages = chestContent.size();
    }

    /**
     * Serialize a city to be saved in the database
     */
    public DBCity serialize() {
        return new DBCity(cityUUID, name, balance, type.name(), powerPoints, freeClaims);
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

    public void rename(String newName) {
        this.name = newName;

        CityManager.saveCity(this);
    }

    public void setType(String type) {
        if ("war".equalsIgnoreCase(type)) {
            this.type = CityType.WAR;
        } else if ("peace".equalsIgnoreCase(type)) {
            this.type = CityType.PEACE;
        }

        CityManager.saveCity(this);
    }

    public void changeType() {
        if (this.type == CityType.WAR) {
            this.type = CityType.PEACE;
        } else if (this.type == CityType.PEACE) {
            this.type = CityType.WAR;
        }

        CityManager.saveCity(this);
    }

    // ==================== Members Methods ====================

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
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.addPlayerToCity(this, player);
        });
    }

    /**
     * Allows a player to leave a city and updates the database and region
     * permissions.
     *
     * @param player The UUID of the player leaving the city.
     */
    public void removePlayer(UUID player) {
        members.remove(player);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager()
                    .callEvent(new MemberLeaveEvent(CacheOfflinePlayer.getOfflinePlayer(player), this));
        });
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.removePlayerFromCity(this, player);
        });
    }

    /**
     * Changes the owner of a city.
     *
     * @param player The UUID of the new owner.
     */
    public void changeOwner(UUID player) {
        removePermission(getPlayerWithPermission(CPermission.OWNER), CPermission.OWNER);
        addPermission(player, CPermission.OWNER);
    }

    /**
     * Updates the number of free claims of the city
     */
    public void updateFreeClaims(int diff) {
        freeClaims += diff;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
        });
    }

    /**
     * Updates the power of a city
     */
    public void updatePowerPoints(int diff) {
        powerPoints += diff;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
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
        return chestContent.get(page);
    }

    /**
     * Saves the content of a specific chest page for a city.
     * 
     * @param page    The page number of the chest.
     * @param content The content to save as an array of ItemStack.
     */
    public void saveChestContent(int page, ItemStack[] content) {
        chestContent.put(page, content);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveChestPage(this, page, content);
        });
    }

    /**
     * Retrieves the number of pages for a city's chest.
     *
     * @return The number of pages for the city's chest.
     */
    public @NotNull Integer getChestPages() {
        return chestPages;
    }

    /**
     * Upgrades the city's chest by adding a new page and updating the database
     * asynchronously.
     */
    public void upgradeChest() {
        chestPages += 1;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveChestPage(this, chestPages, null);
        });
    }

    // ==================== Chunk Methods ====================

    /**
     * Adds a chunk to the city's claimed chunks and updates the database
     * asynchronously.
     *
     * @param chunk The chunk to be added.
     */
    public void addChunk(Chunk chunk) {
        BlockVector2 coords = BlockVector2.at(chunk.getX(), chunk.getZ());
        if (chunks.contains(coords))
            return;
        chunks.add(coords);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.claimChunk(this, coords);
        });
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new ChunkClaimedEvent(this, chunk));
        });
    }

    /**
     * Removes a chunk from the city's claimed chunks and updates the database
     * asynchronously.
     *
     * @param chunkX The X coordinate of the chunk to be removed.
     * @param chunkZ The Z coordinate of the chunk to be removed.
     * @return True if the chunk was successfully removed, false otherwise.
     */
    public void removeChunk(Chunk chunk) {
        BlockVector2 coords = BlockVector2.at(chunk.getX(), chunk.getZ());
        if (!chunks.contains(coords))
            chunks.remove(coords);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.unclaimChunk(this, coords);
        });
    }

    /**
     * Checks if a specific chunk is claimed by the city.
     *
     * @param chunkX The X coordinate of the chunk to check.
     * @param chunkZ The Z coordinate of the chunk to check.
     * @return True if the chunk is claimed, false otherwise.
     */
    public boolean hasChunk(Chunk chunk) {
        return chunks.contains(BlockVector2.at(chunk.getX(), chunk.getZ()));
    }

    // ==================== Mayor Methods ====================

    /**
     * Retrieves the mayor of the city.
     *
     * @return The mayor of the city, or null if not found.
     */
    public Mayor getMayor() {
        return MayorManager.cityMayor.get(CityManager.getCity(cityUUID));
    }

    /**
     * Checks if the city has a mayor.
     *
     * @return True if the city has a mayor, false otherwise.
     */
    public boolean hasMayor() {
        Mayor mayor = MayorManager.cityMayor.get(this);
        if (mayor == null)
            return false;

        return mayor.getUUID() != null;
    }

    /**
     * Retrieves the election type of the city.
     *
     * @return The election type of the city, or null if not found.
     */
    public ElectionType getElectionType() {
        Mayor mayor = MayorManager.cityMayor.get(this);
        if (mayor == null)
            return null;

        return mayor.getElectionType();
    }

    /**
     * Retrieves the law of the city.
     *
     * @return The law of the city, or null if not found.
     */
    public CityLaw getLaw() {
        return MayorManager.cityLaws.get(CityManager.getCity(cityUUID));
    }

    // ==================== Economy Methods ====================

    /**
     * Sets the balance for a given City and updates it in the database
     * asynchronously.
     *
     * @param value The new balance value to be set.
     */
    public void setBalance(double value) {
        balance = value;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
        });
    }

    /**
     * Updates the balance for a given City by adding a difference amount and
     * updating it in the database asynchronously.
     *
     * @param diff The amount to be added to the existing balance.
     */
    public void updateBalance(double diff) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityMoneyUpdateEvent(this, balance, balance + diff));
        });
        setBalance(balance + diff);
    }

    /**
     * Adds money to the city bank and removes it from {@link Player}
     * 
     * @param player The player depositing into the bank
     * @param input  The input string to get the money value
     */
    public void depositCityBank(Player player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (EconomyManager.withdrawBalance(player.getUniqueId(), moneyDeposit)) {
                updateBalance(moneyDeposit);
                MessagesManager.sendMessage(player,
                        Component.text("Tu as transféré §d" + EconomyManager.getFormattedSimplifiedNumber(moneyDeposit)
                                + "§r" + EconomyManager.getEconomyIcon() + " à ta ville"),
                        Prefix.CITY, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(),
                        Prefix.CITY, MessageType.ERROR, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY,
                    MessageType.ERROR, true);
        }
    }

    /**
     * Removes money from the city bank and add it to {@link Player}
     * 
     * @param player The player withdrawing from the bank
     * @param input  The input string to get the money value
     */
    public void withdrawCityBank(Player player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (getBalance() < moneyDeposit) {
                MessagesManager.sendMessage(player, Component.text("Ta ville n'a pas assez d'argent en banque"),
                        Prefix.CITY, MessageType.ERROR, false);
            } else {
                updateBalance(-moneyDeposit);
                EconomyManager.addBalance(player.getUniqueId(), moneyDeposit);
                MessagesManager.sendMessage(player,
                        Component.text("§d" + EconomyManager.getFormattedSimplifiedNumber(moneyDeposit) + "§r"
                                + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"),
                        Prefix.CITY, MessageType.SUCCESS, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY,
                    MessageType.ERROR, true);
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

        if (MayorManager.phaseMayor == 2) {
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
    public UUID getPlayerWithPermission(CPermission permission) {
        for (UUID player : permissions.keySet()) {
            if (permissions.get(player).contains(permission)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Retrieves the permissions for a specific player.
     *
     * @param player The UUID of the player to retrieve permissions for.
     * @return A set of permissions for the player.
     */
    public Set<CPermission> getPermissions(UUID player) {
        return permissions.get(player);
    }

    /**
     * Checks if a player has a specific permission.
     *
     * @param uuid       The UUID of the player to check.
     * @param permission The permission to check for.
     * @return True if the player has the permission, false otherwise.
     */
    public boolean hasPermission(UUID uuid, CPermission permission) {
        Set<CPermission> playerPerms = permissions.get(uuid);

        if (playerPerms.contains(CPermission.OWNER))
            return true;

        return playerPerms.contains(permission);
    }

    /**
     * Adds a specific permission to a player and updates the database
     * asynchronously.
     *
     * @param uuid       The UUID of the player to add the permission to.
     * @param permission The permission to add.
     */
    public void addPermission(UUID player, CPermission permission) {
        Set<CPermission> playerPerms = permissions.getOrDefault(player, new HashSet<>());

        if (playerPerms.contains(permission))
            return;

        playerPerms.add(permission);
        permissions.put(player, playerPerms);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.addPlayerPermission(this, player, permission);
        });

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(
                    new CityPermissionChangeEvent(this, CacheOfflinePlayer.getOfflinePlayer(player), permission, true));
        });
    }

    /**
     * Removes a specific permission from a player and updates the database
     * asynchronously.
     *
     * @param uuid       The UUID of the player to remove the permission from.
     * @param permission The permission to remove.
     */
    public void removePermission(UUID player, CPermission permission) {
        Set<CPermission> playerPerms = permissions.get(player);

        if (playerPerms == null)
            return;

        if (playerPerms.contains(permission)) {
            playerPerms.remove(permission);
            permissions.put(player, playerPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                CityManager.removePlayerPermission(this, player, permission);
            });
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new CityPermissionChangeEvent(this,
                        CacheOfflinePlayer.getOfflinePlayer(player), permission, false));
            });
        }
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
}

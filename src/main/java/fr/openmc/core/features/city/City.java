package fr.openmc.core.features.city;

import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.events.*;
import fr.openmc.core.features.city.models.DBCity;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
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
import java.util.stream.Collectors;

public class City {
    @Getter
    private String name;
    private final String cityUUID;
    private Set<UUID> members;
    private Set<BlockVector2> chunks; // Liste des chunks claims par la ville
    private HashMap<UUID, Set<CPermission>> permissions;
    private HashMap<Integer, ItemStack[]> chestContent;
    @Getter
    @Setter
    private UUID chestWatcher;
    @Getter
    private double balance;
    @Getter
    private CityType type;
    @Getter
    private int powerPoints;
    @Getter
    private int freeClaims;

    /**
     * Constructor used for City creation
     */
    public City(String id, String name, Player owner, CityType type, Chunk chunk) {
        this.cityUUID = id;
        this.name = name;
        this.type = type;
        this.freeClaims = 15;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
        });

        CityManager.registerCity(this);

        this.members = new HashSet<>();
        this.permissions = new HashMap<>();
        this.chunks = new HashSet<>();
        this.chestContent = new HashMap<>();

        addChunk(chunk);
        addPlayer(owner.getUniqueId());
        addPermission(owner.getUniqueId(), CPermission.OWNER);
        saveChestContent(1, null);

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityCreationEvent(this, owner));
        });
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
    }

    /**
     * Serialize a city to be saved in the database
     */
    public DBCity serialize() {
        return new DBCity(cityUUID, name, balance, type.name(), powerPoints, freeClaims);
    }

    // ==================== Global Methods ====================

    /**
     * Gets all the member of this city
     */
    public Set<UUID> getMembers() {
        if (this.members == null)
            this.members = CityManager.getCityMembers(this);

        return this.members;
    }

    /**
     * Gets all the member of this city
     */
    public Set<BlockVector2> getChunks() {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        return this.chunks;
    }

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

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
        });
    }

    public void setType(String type) {
        if ("war".equalsIgnoreCase(type)) {
            this.type = CityType.WAR;
        } else if ("peace".equalsIgnoreCase(type)) {
            this.type = CityType.PEACE;
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
        });
    }

    public void changeType() {
        if (this.type == CityType.WAR) {
            this.type = CityType.PEACE;
        } else if (this.type == CityType.PEACE) {
            this.type = CityType.WAR;
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
        });
    }

    // ==================== Members Methods ====================

    /**
     * Gets the list of online members (UUIDs) of a specific city.
     *
     * @return A list of UUIDs representing the online members of the city.
     */
    public Set<UUID> getOnlineMembers() {
        Set<UUID> allMembers = getMembers();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .filter(allMembers::contains)
                .collect(Collectors.toSet());
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
        if (this.members == null)
            this.members = CityManager.getCityMembers(this);

        members.add(player);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new MemberJoinEvent(CacheOfflinePlayer.getOfflinePlayer(player), this));
        });
        CityManager.addPlayerToCity(this, player);
    }

    /**
     * Allows a player to leave a city and updates the database and region
     * permissions.
     *
     * @param player The UUID of the player leaving the city.
     */
    public void removePlayer(UUID player) {
        if (this.members == null)
            this.members = CityManager.getCityMembers(this);

        members.remove(player);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager()
                    .callEvent(new MemberLeaveEvent(CacheOfflinePlayer.getOfflinePlayer(player), this));
        });
        CityManager.removePlayerFromCity(this, player);
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

    // ==================== Chest Methods ====================

    /**
     * Gets the content of a specific chest page for a city.
     *
     * @param page The page number of the chest.
     * @return The content of the chest page as an array of ItemStack.
     */
    public ItemStack[] getChestContent(int page) {
        if (this.chestContent == null)
            this.chestContent = CityManager.getCityChestContent(this);

        if (page > getChestPages())
            page = getChestPages();

        return chestContent.get(page);
    }

    /**
     * Saves the content of a specific chest page for a city.
     *
     * @param page    The page number of the chest.
     * @param content The content to save as an array of ItemStack.
     */
    public void saveChestContent(int page, ItemStack[] content) {
        if (this.chestContent == null)
            this.chestContent = CityManager.getCityChestContent(this);

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
        if (this.chestContent == null)
            this.chestContent = CityManager.getCityChestContent(this);

        if (this.chestContent.size() < 1)
            saveChestContent(1, null);

        return chestContent.size();
    }

    // ==================== Chunk Methods ====================

    /**
     * Adds a chunk to the city's claimed chunks and updates the database
     * asynchronously.
     *
     * @param chunk The chunk to be added.
     */
    public void addChunk(Chunk chunk) {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        BlockVector2 coords = BlockVector2.at(chunk.getX(), chunk.getZ());
        if (chunks.contains(coords))
            return;
        chunks.add(coords);

        CityManager.claimChunk(this, coords);
    }

    /**
     * Adds a chunk to the city's claimed chunks by specifying its coordinates and updates the database asynchronously.
     *
     * @param x The X coordinate of the chunk to be added.
     * @param z The Z coordinate of the chunk to be added.
     */
    public void addChunk(int x, int z) {
        Chunk chunk = Bukkit.getWorld("world").getChunkAt(x, z);
        addChunk(chunk);
    }

    /**
     * Removes a chunk from the city's claimed chunks and updates the database
     * asynchronously.
     *
     * @param chunk The chunk to be removed.
     */
    public void removeChunk(Chunk chunk) {
        removeChunk(chunk.getX(), chunk.getZ());
    }

    /**
     * Removes a chunk from the city's claimed chunks and updates the database
     * asynchronously.
     *
     * @param chunkX The X coordinate of the chunk to be removed.
     * @param chunkZ The Z coordinate of the chunk to be removed.
     */
    public void removeChunk(int chunkX, int chunkZ) {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        BlockVector2 coords = BlockVector2.at(chunkX, chunkZ);
        chunks.remove(coords);

        CityManager.unclaimChunk(this, coords);
    }

    /**
     * Checks if a specific chunk is claimed by the city.
     *
     * @param x The X coordinate of the chunk to check.
     * @param z The Z coordinate of the chunk to check.
     * @return True if the chunk is claimed, false otherwise.
     */
    public boolean hasChunk(double x, double z) {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        return chunks.contains(BlockVector2.at(x, z));
    }

    /**
     * Checks if a specific chunk is claimed by the city.
     *
     * @param chunk The chunk
     * @return True if the chunk is claimed, false otherwise.
     */
    public boolean hasChunk(Chunk chunk) {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        return chunks.contains(BlockVector2.at(chunk.getX(), chunk.getZ()));
    }

    // ==================== Economy Methods ====================

    /**
     * Sets the balance for a given City and updates it in the database
     * asynchronously.
     *
     * @param value The new balance value to be set.
     */
    public void setBalance(double value) {
        double before = balance;
        balance = value;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
        });
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityMoneyUpdateEvent(this, before, balance));
        });
    }

    /**
     * Updates the balance for a given City by adding a difference amount and
     * updating it in the database asynchronously.
     *
     * @param diff The amount to be added to the existing balance.
     */
    public void updateBalance(double diff) {
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
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

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
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

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
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

        Set<CPermission> playerPerms = permissions.getOrDefault(uuid, new HashSet<>());

        if (playerPerms.contains(CPermission.OWNER))
            return true;

        return playerPerms.contains(permission);
    }

    /**
     * Adds a specific permission to a player and updates the database
     * asynchronously.
     *
     * @param playerUUID       The UUID of the player to add the permission to.
     * @param permission The permission to add.
     */
    public void addPermission(UUID playerUUID, CPermission permission) {
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

        Set<CPermission> playerPerms = permissions.getOrDefault(playerUUID, new HashSet<>());

        if (playerPerms.contains(permission))
            return;

        playerPerms.add(permission);
        permissions.put(playerUUID, playerPerms);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.addPlayerPermission(this, playerUUID, permission);
        });

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(
                    new CityPermissionChangeEvent(this, CacheOfflinePlayer.getOfflinePlayer(playerUUID), permission, true));
        });
    }

    /**
     * Removes a specific permission from a player and updates the database
     * asynchronously.
     *
     * @param playerUUID       The UUID of the player to remove the permission from.
     * @param permission The permission to remove.
     */
    public void removePermission(UUID playerUUID, CPermission permission) {
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

        Set<CPermission> playerPerms = permissions.get(playerUUID);

        if (playerPerms == null)
            return;

        if (!playerPerms.contains(permission))
            return;

        playerPerms.remove(permission);
        permissions.put(playerUUID, playerPerms);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.removePlayerPermission(this, playerUUID, permission);
        });
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityPermissionChangeEvent(this,
                    CacheOfflinePlayer.getOfflinePlayer(playerUUID), permission, false));
        });
    }

    // ==================== Mascots Methods ====================

    public Mascot getMascot() {
        return MascotsManager.mascotsByCityUUID.get(cityUUID);
    }

    // ==================== Mayor Methods ====================

    /**
     * Retrieves the mayor of the city.
     *
     * @return The mayor of the city, or null if not found.
     */
    public Mayor getMayor() {
        return MayorManager.cityMayor.get(this.getUUID());
    }

    /**
     * Checks if the city has a mayor.
     *
     * @return True if the city has a mayor, false otherwise.
     */
    public boolean hasMayor() {
        Mayor mayor = MayorManager.cityMayor.get(this.getUUID());
        if (mayor == null) return false;

        return mayor.getUUID() != null;
    }

    /**
     * Retrieves the election type of the city.
     *
     * @return The election type of the city, or null if not found.
     */
    public ElectionType getElectionType() {
        Mayor mayor = MayorManager.cityMayor.get(this.getUUID());
        if (mayor == null) return null;

        return mayor.getElectionType();
    }

    /**
     * Retrieves the law of the city.
     *
     * @return The law of the city, or null if not found.
     */
    public CityLaw getLaw() {
        return MayorManager.cityLaws.get(cityUUID);
    }

    // ==================== War Methods ====================

    /**
     * Retrieves the power points of the city.
     *
     * @return The power points of the city, or 0 if not found.
     */
    public boolean isInWar() {
        return WarManager.isCityInWar(cityUUID);
    }

    /**
     * Retrieves the power points of the city.
     *
     * @return The power points of the city, or 0 if not found.
     */
    public War getWar() {
        return WarManager.getWarByCity(cityUUID);
    }

    /**
     * Checks if the city is immune.
     *
     * @return True if the city is immune, false otherwise.
     */
    public boolean isImmune() {
        return getMascot().isImmunity() && !DynamicCooldownManager.isReady(cityUUID, "city:immunity");
    }

    /**
     * Updates the power of a City by adding or removing points.
     *
     * @param point The amount to be added or remove to the existing power.
     */
    /**
     * Updates the power of a city
     */
    public void updatePowerPoints(int diff) {
        powerPoints += diff;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            CityManager.saveCity(this);
        });
    }
}

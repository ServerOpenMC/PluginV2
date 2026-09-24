package fr.openmc.core.features.city.models.city;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.events.*;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.CityType;
import fr.openmc.core.features.city.models.city.namespaces.*;
import fr.openmc.core.features.city.models.db.DBCity;
import fr.openmc.core.features.city.models.db.DBCityRank;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;
import fr.openmc.core.features.city.sub.milestone.rewards.RankLimitRewards;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.features.city.sub.rank.CityRankManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.statistics.models.CityStatistics;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.city.sub.war.models.WarHistory;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.cache.CachePlayerName;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.chunk.ChunkPos;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import static fr.openmc.core.features.city.actions.CityCreateAction.FREE_CLAIMS;

public class City implements CityInterface, CityChunks, CityMembers, CityChest,
        CityRanks, CityPermissions, CityEconomy, CityWar, CityNotations,
        CityStatistic, CityMayor {

    private final UUID uniqueId;
    private String name;
    private Set<UUID> members;
    private Set<ChunkPos> chunks;
    private HashMap<UUID, Set<CityPermission>> permissions;
    private final Set<DBCityRank> cityRanks = new HashSet<>();
    private HashMap<Integer, ItemStack[]> chestContent;
    private UUID chestWatcher;
    private double balance;
    private CityType type;
    private int powerPoints;
    private int freeClaims;
    private int level;

    private static final CityManager cityManager = OMCRegistry.FEATURES.CITY.get();
    private static final MayorManager mayorManager = OMCRegistry.CITY_FEATURES.MAYOR;
    private static final CityRankManager cityRankManager = OMCRegistry.CITY_FEATURES.RANKS;
    private static final NotationManager notationManager = OMCRegistry.CITY_FEATURES.NOTATION;
    private static final CityBankManager cityBankManager = OMCRegistry.CITY_FEATURES.CITY_BANK;
    private static final WarManager warManager = OMCRegistry.CITY_FEATURES.WAR;
    private static final CityStatisticsManager statisticsManager = OMCRegistry.CITY_FEATURES.STATS;
    private static final MascotsManager mascotsManager = OMCRegistry.CITY_FEATURES.MASCOTS;

    /**
     * Constructor used for City creation
     */
    public City(UUID uniqueId, String name, Player owner, CityType type, Chunk chunk) {

        this.uniqueId = uniqueId;
        this.name = name;
        this.type = type;
        this.freeClaims = FREE_CLAIMS;
        this.level = 1;

        this.members = new HashSet<>();
        this.permissions = new HashMap<>();
        this.chunks = new HashSet<>();
        this.chestContent = new HashMap<>();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.saveCity(this)
        );

        cityManager.registerCity(this);

        addChunk(chunk.getX(), chunk.getZ());

        addPlayer(owner.getUniqueId());
        addPermission(owner.getUniqueId(), CityPermission.OWNER);
        saveChestContent(1, null);
        cityRankManager.loadCityRanks(this);

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CityCreationEvent(this, owner))
        );
    }

    /**
     * Constructor used to deserialize a City database object
     */
    public City(UUID uniqueId, String name, double balance, String type, int power, int freeClaims, int level) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.balance = balance;
        this.freeClaims = freeClaims;
        this.powerPoints = power;
        this.type = CityType.valueOf(type.toUpperCase());
        this.level = level;

        cityManager.registerCity(this);
    }

    public static City of(String name) {
        return OMCRegistry.FEATURES.CITY.get().getCityByName(name);
    }

    public static City of(ChunkPos chunkPos) {
        return OMCRegistry.FEATURES.CITY.get().getCityFromChunk(chunkPos);
    }

    public static City of(Block block) {
        return OMCRegistry.FEATURES.CITY.get().getCityFromChunk(block.getChunk());
    }

    public static City of(Location location) {
        return OMCRegistry.FEATURES.CITY.get().getCityFromChunk(location.getChunk());
    }

    public static City of(Chunk chunk) {
        return OMCRegistry.FEATURES.CITY.get().getCityFromChunk(chunk);
    }

    public static City of(int x, int z) {
        return OMCRegistry.FEATURES.CITY.get().getCityFromChunk(x, z);
    }

    public static City of(UUID cityUUID) {
        return OMCRegistry.FEATURES.CITY.get().getCity(cityUUID);
    }

    public static City ofPlayer(Player player) {
        return OMCRegistry.FEATURES.CITY.get().getPlayerCity(player.getUniqueId());
    }

    public static City ofPlayer(UUID playerUUID) {
        return OMCRegistry.FEATURES.CITY.get().getPlayerCity(playerUUID);
    }

    public static City ofMascot(UUID entityUUID) {
        return OMCRegistry.CITY_FEATURES.MASCOTS.getCityFromEntity(entityUUID);
    }

    @Override
    public DBCity serialize() {
        return new DBCity(uniqueId, name, balance, type.name(), powerPoints, freeClaims, level);
    }

    // ** Infos globales

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void rename(String newName) {
        cityManager.updateCityByName(this, newName);

        this.name = newName;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.saveCity(this)
        );
    }

    @Override
    public CityType getType() {
        return type;
    }

    @Override
    public void changeType() {
        if (this.type == CityType.WAR) this.type = CityType.PEACE;
        else if (this.type == CityType.PEACE) this.type = CityType.WAR;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.saveCity(this)
        );
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int newLevel) {
        this.level = newLevel;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.saveCity(this)
        );
    }

    @Override
    public int getFreeClaims() {
        return freeClaims;
    }

    @Override
    public void updateFreeClaims(int diff) {
        freeClaims += diff;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.saveCity(this)
        );
    }

    @Override
    public int getPowerPoints() {
        return powerPoints;
    }

    @Override
    public void updatePowerPoints(int diff) {
        powerPoints += diff;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.saveCity(this)
        );
    }

    @Override
    public @Nullable UUID getChestWatcher() {
        return chestWatcher;
    }

    @Override
    public void setChestWatcher(UUID chestWatcher) {
        this.chestWatcher = chestWatcher;
    }

    @Override
    public Set<UUID> getMembers() {
        if (this.members == null)
            this.members = cityManager.getCityMembers(this);
        return this.members;
    }

    @Override
    public Set<UUID> getOnlineMembers() {
        Set<UUID> allMembers = getMembers();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .filter(allMembers::contains)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isMember(Player player) {
        return getMembers().contains(player.getUniqueId());
    }

    @Override
    public void addPlayer(UUID player) {
        getMembers().add(player);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new MemberJoinEvent(CacheOfflinePlayer.getOfflinePlayer(player), this))
        );
        cityManager.addPlayerToCity(this, player);
    }

    @Override
    public void removePlayer(UUID playerUUID) {
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);

        if (offlinePlayer.isOnline() && offlinePlayer instanceof Player player)
            player.closeInventory();

        getMembers().remove(playerUUID);
        clearPermissions(playerUUID);

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new MemberLeaveEvent(offlinePlayer, this))
        );
        cityManager.removePlayerFromCity(this, playerUUID);
    }

    @Override
    public void changeOwner(UUID player) {
        removePermission(getPlayerWithPermission(CityPermission.OWNER), CityPermission.OWNER);
        addPermission(player, CityPermission.OWNER);
    }

    @Override
    public Set<ChunkPos> getChunks() {
        if (this.chunks == null)
            this.chunks = cityManager.getCityChunks(this);
        return this.chunks;
    }

    @Override
    public void addChunk(int x, int z) {
        ChunkPos chunkPos = new ChunkPos(x, z);
        if (getChunks().contains(chunkPos))
            return;

        getChunks().add(chunkPos);
        cityManager.claimChunk(this, chunkPos);
    }

    @Override
    public void removeChunk(int x, int z) {
        ChunkPos chunkPos = new ChunkPos(x, z);
        getChunks().remove(chunkPos);
        cityManager.unclaimChunk(this, chunkPos);
    }

    @Override
    public boolean hasChunk(int x, int z) {
        return getChunks().contains(new ChunkPos(x, z));
    }

    @Override
    public ItemStack[] getChestContent(int page) {
        if (this.chestContent == null)
            this.chestContent = cityManager.getCityChestContent(this);

        if (page > getChestPages())
            page = getChestPages();

        return chestContent.get(page);
    }

    @Override
    public void saveChestContent(int page, ItemStack[] content) {
        if (this.chestContent == null)
            this.chestContent = cityManager.getCityChestContent(this);

        chestContent.put(page, content);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.saveChestPage(this, page, content)
        );
    }

    @Override
    public @NotNull Integer getChestPages() {
        if (this.chestContent == null)
            this.chestContent = cityManager.getCityChestContent(this);

        if (this.chestContent.isEmpty())
            saveChestContent(1, null);

        return chestContent.size();
    }

    @Override
    public UUID getPlayerWithPermission(CityPermission permission) {
        if (this.permissions == null)
            this.permissions = cityManager.getCityPermissions(this);

        for (UUID player : permissions.keySet()) {
            if (permissions.get(player).contains(permission))
                return player;
        }
        return null;
    }

    @Override
    public Set<CityPermission> getPermissions(UUID player) {
        if (this.permissions == null)
            this.permissions = cityManager.getCityPermissions(this);

        return permissions.get(player);
    }

    @Override
    public boolean hasPermission(UUID uuid, CityPermission permission) {
        if (this.permissions == null)
            this.permissions = cityManager.getCityPermissions(this);

        Set<CityPermission> playerPerms = permissions.getOrDefault(uuid, new HashSet<>());
        return playerPerms.contains(CityPermission.OWNER) || playerPerms.contains(permission);
    }

    @Override
    public void addPermission(UUID playerUUID, CityPermission permission) {
        if (this.permissions == null)
            this.permissions = cityManager.getCityPermissions(this);

        Set<CityPermission> playerPerms = permissions.getOrDefault(playerUUID, new HashSet<>());
        if (playerPerms.contains(permission))
            return;

        playerPerms.add(permission);
        permissions.put(playerUUID, playerPerms);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.addPlayerPermission(this, playerUUID, permission)
        );
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(
                        new CityPermissionChangeEvent(this, CacheOfflinePlayer.getOfflinePlayer(playerUUID), permission, true))
        );
    }

    @Override
    public void removePermission(UUID playerUUID, CityPermission permission) {
        if (this.permissions == null)
            this.permissions = cityManager.getCityPermissions(this);

        Set<CityPermission> playerPerms = permissions.get(playerUUID);
        if (playerPerms == null || !playerPerms.contains(permission))
            return;

        playerPerms.remove(permission);
        permissions.put(playerUUID, playerPerms);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.removePlayerPermission(this, playerUUID, permission));
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CityPermissionChangeEvent(this,
                        CacheOfflinePlayer.getOfflinePlayer(playerUUID), permission, false)));
    }

    @Override
    public void clearPermissions(UUID playerUUID) {
        if (this.permissions == null)
            this.permissions = cityManager.getCityPermissions(this);

        permissions.remove(playerUUID);
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double value) {
        double before = this.balance;
        this.balance = value;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                cityManager.saveCity(this)
        );
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CityMoneyUpdateEvent(this, before, this.balance))
        );
    }

    @Override
    public void updateBalance(double diff) {
        setBalance(balance + diff);
    }

    @Override
    public void depositCityBank(Player player, String input) {
        cityBankManager.depositCityBank(this, player, input);
    }

    @Override
    public void withdrawCityBank(Player player, String input) {
        cityBankManager.withdrawCityBank(this, player, input);
    }

    @Override
    public double calculateCityInterest() {
        return cityBankManager.calculateCityInterest(this);
    }

    @Override
    public void applyCityInterest() {
        cityBankManager.applyCityInterest(this);
    }

    @Override
    public Mascot getMascot() {
        return mascotsManager.getMascotsByCityUUID().get(this.getUniqueId());
    }

    @Override
    public boolean isImmune() {
        if (this.getMascot() == null) return false;
        return this.getMascot().isImmunity() && !DynamicCooldownManager.isReady(this.getUniqueId(), "city:immunity");
    }

    @Override
    public MayorPhase getMayorPhase() {
        return mayorManager.getMayorPhase();
    }

    @Override
    public Mayor getMayor() {
        return mayorManager.getCityMayor().get(this.getUniqueId());
    }

    @Override
    public boolean hasMayor() {
        Mayor mayor = mayorManager.getCityMayor().get(this.getUniqueId());
        return mayor != null && mayor.getMayorUUID() != null;
    }

    @Override
    public ElectionType getElectionType() {
        Mayor mayor = mayorManager.getCityMayor().get(this.getUniqueId());
        return mayor == null ? null : mayor.getElectionType();
    }

    @Override
    public CityLaw getLaw() {
            return mayorManager.getCityLaws().get(this.getUniqueId());
    }

    @Override
    public boolean isInWar() {
        return warManager.isCityInWar(this.getUniqueId());
    }

    @Override
    public War getWar() {
        return warManager.getWarByCity(this.getUniqueId());
    }

    @Override
    public WarHistory getWarHistory() {
        return warManager.warHistory.get(this.uniqueId);
    }

    @Override
    public Set<DBCityRank> getRanks() {
        return cityRanks;
    }

    @Override
    public boolean isRanksFull() {
        return cityRanks.size() >= RankLimitRewards.getRankLimit(this.getLevel());
    }

    @Override
    public DBCityRank getRankByName(String rankName) {
        for (DBCityRank rank : cityRanks) {
            if (rank.getName().equalsIgnoreCase(rankName))
                return rank;
        }
        return null;
    }

    @Override
    public boolean isRankExists(DBCityRank rank) {
        return cityRanks.contains(rank);
    }

    @Override
    public boolean isRankExists(String rankName) {
        return getRankByName(rankName) != null;
    }

    @Override
    public void createRank(DBCityRank rank) {
        if (isRanksFull())
            throw new IllegalStateException("Cannot add more than 18 ranks to a city.");

        cityRanks.add(rank);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> cityRankManager.addCityRank(rank));
    }

    @Override
    public void deleteRank(DBCityRank rank) {
        if (!cityRanks.contains(rank))
            throw new IllegalArgumentException("Rank not found in the city's ranks.");
        if (rank.getPriority() == 0)
            throw new IllegalArgumentException("Cannot delete the default rank (priority 0).");

        cityRanks.remove(rank);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> cityRankManager.removeCityRank(rank));
    }

    @Override
    public void updateRank(DBCityRank oldRank, DBCityRank newRank) {
        if (!cityRanks.contains(oldRank))
            throw new IllegalArgumentException("Old rank not found in the city's ranks.");

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> cityRankManager.updateCityRank(newRank));
        cityRanks.remove(oldRank);
        cityRanks.add(newRank);
    }

    @Override
    public @Nullable DBCityRank getRankOfMember(UUID member) {
        for (DBCityRank rank : cityRanks) {
            if (rank.getMembersSet().contains(member))
                return rank;
        }
        return null;
    }

    @Override
    public String getRankName(UUID member) {
        if (this.hasPermission(member, CityPermission.OWNER))
            return "Propriétaire";
        if (this.hasMayor() && this.getMayor().getMayorUUID().equals(member))
            return "Maire";

        DBCityRank rank = getRankOfMember(member);
        return rank != null ? rank.getName() : "Membre";
    }

    @Override
    public void changeRank(Player sender, UUID playerUUID, DBCityRank newRank) {
        if (!cityRanks.contains(newRank))
            throw new IllegalArgumentException("The specified rank does not exist in the city's ranks.");

        if (hasPermission(playerUUID, CityPermission.OWNER)) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.player_is_owner"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        DBCityRank currentRank = getRankOfMember(playerUUID);

        if (currentRank != null) {
            currentRank.removeMember(playerUUID);
            for (CityPermission permission : currentRank.getPermissionsSet())
                removePermission(playerUUID, permission);

            MessagesManager.sendMessage(sender,
                    TranslationManager.translation("feature.city.grade.remove_grade",
                            Component.text(currentRank.getName()).color(NamedTextColor.YELLOW),
                            CachePlayerName.name(playerUUID).color(NamedTextColor.GOLD)),
                    Prefix.CITY, MessageType.SUCCESS, true);
        }

        if (currentRank != newRank) {
            newRank.addMember(playerUUID);
            for (CityPermission permission : newRank.getPermissionsSet())
                addPermission(playerUUID, permission);

            MessagesManager.sendMessage(sender,
                    TranslationManager.translation("feature.city.grade.assign_grade",
                            Component.text(newRank.getName()).color(NamedTextColor.YELLOW),
                            CachePlayerName.name(playerUUID).color(NamedTextColor.GOLD)),
                    Prefix.CITY, MessageType.SUCCESS, true);
        }

        DBCityRank finalCurrentRank = currentRank;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            if (finalCurrentRank != null)
                cityRankManager.updateCityRank(finalCurrentRank);
            cityRankManager.updateCityRank(newRank);
        });
    }

    @Override
    public boolean isTop10Notation() {
        return notationManager.top10Cities.contains(this.uniqueId);
    }

    @Override
    public @Nullable CityNotation getNotationOfWeek(String weekStr) {
        if (!notationManager.notationPerWeek.containsKey(weekStr))
            return null;

        return notationManager.notationPerWeek.get(weekStr).stream()
                .filter(notation -> notation.getCityUUID().equals(this.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CityNotation> getAvailableNotation() {
        if (notationManager.cityNotations.get(this.getUniqueId()) == null) return List.of();

        return notationManager.cityNotations.get(this.getUniqueId()).stream()
                .filter(notation -> notation.getCityUUID().equals(this.getUniqueId()))
                .filter(notation -> DateUtils.isBefore(notation.getWeekStr(), DateUtils.getWeekFormat()))
                .toList();
    }

    @Override
    public void setNotationOfWeek(String weekStr, double architecturalNote, double coherenceNote, String description) {
        notationManager.createOrUpdateNotation(
                new CityNotation(this.getUniqueId(), architecturalNote, coherenceNote, description, weekStr));
    }

    @Override
    public CityStatistics getOrCreateStat(String scope) {
        return statisticsManager.getOrCreateStat(this.uniqueId, scope);
    }

    @Override
    public void setStat(String scope, Serializable value) {
        statisticsManager.setStat(this.uniqueId, scope, value);
    }

    @Override
    public void removeStats() {
        statisticsManager.removeStats(this.uniqueId);
    }

    @Override
    public void incrementStats(String scope, long amount) {
        statisticsManager.increment(this.uniqueId, scope, amount);
    }

    @Override
    public Object getStatValue(String scope) {
        return statisticsManager.getStatValue(this.uniqueId, scope);
    }


    @Override
    public CityMembers members() {
        return this;
    }

    @Override
    public CityChunks chunks() {
        return this;
    }

    @Override
    public CityChest chest() {
        return this;
    }

    @Override
    public CityPermissions permissions() {
        return this;
    }

    @Override
    public CityRanks ranks() {
        return this;
    }

    @Override
    public CityEconomy economy() {
        return this;
    }

    @Override
    public CityMayor mayor() {
        return this;
    }

    @Override
    public CityNotations notation() {
        return this;
    }

    @Override
    public CityStatistic statistic() {
        return this;
    }

    @Override
    public CityWar war() {
        return this;
    }
}

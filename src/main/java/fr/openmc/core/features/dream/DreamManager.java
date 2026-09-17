package fr.openmc.core.features.dream;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.perks.PerkUtils;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.dream.commands.AdminDreamCommands;
import fr.openmc.core.features.dream.commands.DreamCommands;
import fr.openmc.core.features.dream.listeners.dream.*;
import fr.openmc.core.features.dream.listeners.registry.CraftingConvertorListener;
import fr.openmc.core.features.dream.listeners.registry.DreamItemEquipListener;
import fr.openmc.core.features.dream.listeners.structures.CloudStructureDispenserListener;
import fr.openmc.core.features.dream.listeners.structures.PlayerDreamStructureListener;
import fr.openmc.core.features.dream.listeners.structures.ReplaceBlockListener;
import fr.openmc.core.features.dream.mecanism.rng.DreamLootListener;
import fr.openmc.core.features.dream.mecanism.singularity.SingularityCraftListener;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DBPlayerSave;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.*;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.interfaces.HasRegistries;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.lifecycle.registries.LifecycleRegistry;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import fr.openmc.core.utils.bukkit.serializer.BukkitSerializer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.LocationUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

@Credit(developers = {"iambibi_", "gab400"}, graphist = {"Tfloa"}, builders = {"Mcross_bow"})
public class DreamManager extends Feature
        implements HasDatabase, HasCommands, HasListeners, HasRegistries {
    // ** CONSTANTS **
    public static final Long BASE_DREAM_TIME = 300L;

    private final HashMap<UUID, DBPlayerSave> playerSaveData = new HashMap<>();
    private final HashMap<UUID, DreamPlayer> dreamPlayerData = new HashMap<>();
    public final HashMap<UUID, DBDreamPlayer> cacheDreamPlayer = new HashMap<>();

    private Dao<DBDreamPlayer, String> dreamPlayerDao;
    private Dao<DBPlayerSave, String> savePlayerDao;

    @Override
    public void init() {
        // ** LOAD DATAS **
        loadAllDreamPlayerData();
        loadAllPlayerSaveData();
    }

    @Override
    public List<Supplier<LifecycleRegistry>> getRegistries() {
        return new ArrayList<>(List.of(
                () -> OMCRegistry.DREAM_FEATURES = new DreamFeaturesRegistry(),
                () -> OMCRegistry.DREAM_ITEM = new DreamItemRegistry(),
                () -> OMCRegistry.DREAM_LOOT_TABLE = new DreamLootTableRegistry(),
                () -> OMCRegistry.DREAM_MOB = new DreamMobsRegistry()
        ));
    }

    // ** COMMANDS **
    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new AdminDreamCommands(this),
                new DreamCommands()
        );
    }

    // ** LISTENERS **
    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                () -> new PlayerChangeWorldListener(this),
                () -> new PlayerJoinListener(this),
                () -> new PlayerQuitListener(this),
                () -> new PlayerDeathListener(this),
                PlayerCommandListener::new,
                () -> new PlayerDreamTimeEndListener(this),
                () -> new PlayerSleepListener(this),
                () -> new PlayerEnteredBiome(this),
                () -> new PlayerObtainOrb(this),
                PlayerDamageListener::new,
                ReplaceBlockListener::new,
                () -> new PlayerEatSomnifere(this),
                CloudStructureDispenserListener::new,
                CraftingConvertorListener::new,
                () -> new DreamItemEquipListener(this),
                SingularityCraftListener::new,
                PlayerDreamStructureListener::new,
                () -> new PlayerFoodChangeListener(this),
                DreamLootListener::new,
                PlayerPickupListener::new
        );
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBDreamPlayer.class);
        dreamPlayerDao = DaoManager.createDao(connectionSource, DBDreamPlayer.class);

        TableUtils.createTableIfNotExists(connectionSource, DBPlayerSave.class);
        savePlayerDao = DaoManager.createDao(connectionSource, DBPlayerSave.class);
    }

    @Override
    public void save() {
        this.saveAllPlayerSaveData();
        this.saveAllDreamPlayerData();
    }

    private void loadAllPlayerSaveData() {
        try {
            playerSaveData.clear();
            savePlayerDao.queryForAll().forEach(playerData -> {
                playerSaveData.put(playerData.getPlayerUUID(), playerData);
                try {
                    savePlayerDao.delete(playerData);
                } catch (SQLException e) {
                    OMCLogger.error("Cannot load player save data", e);
                }
            });
        } catch (SQLException e) {
            OMCLogger.error("Cannot load player save data", e);
        }
    }

    public void saveAllPlayerSaveData() {
        playerSaveData.forEach((uuid, playerSave) -> {
            try {
                savePlayerDao.createOrUpdate(playerSave);
            } catch (SQLException e) {
                OMCLogger.error("Cannot save player save data for player {}", uuid, e);
            }
        });
    }


    private void loadAllDreamPlayerData() {
        try {
            dreamPlayerData.clear();
            dreamPlayerDao.queryForAll().forEach(playerData ->
                    cacheDreamPlayer.put(playerData.getPlayerUUID(), playerData)
            );
        } catch (SQLException e) {
            OMCLogger.error("Cannot load dream player data", e);
        }
    }

    public void saveAllDreamPlayerData() {
        cacheDreamPlayer.forEach((uuid, dbDreamPlayer) -> {
            try {
                dreamPlayerDao.createOrUpdate(dbDreamPlayer);
            } catch (SQLException e) {
                OMCLogger.error("Cannot save dream player data", e);
            }
        });
    }

    public void saveDreamPlayerData(DreamPlayer dreamPlayer) {
        saveDreamPlayerData(dreamPlayer.save());
    }

    public void saveDreamPlayerData(DBDreamPlayer dbDreamPlayer) {
        try {
            dreamPlayerDao.createOrUpdate(dbDreamPlayer);
            if (cacheDreamPlayer.containsKey(dbDreamPlayer.getPlayerUUID())) {
                cacheDreamPlayer.replace(dbDreamPlayer.getPlayerUUID(), dbDreamPlayer);
            } else {
                cacheDreamPlayer.put(dbDreamPlayer.getPlayerUUID(), dbDreamPlayer);
            }

        } catch (SQLException e) {
            OMCLogger.error("Cannot save player save data", e);
        }
    }

    public DBDreamPlayer getCacheDreamPlayer(Player player) {
        if (!cacheDreamPlayer.containsKey(player.getUniqueId())) return null;

        return cacheDreamPlayer.get(player.getUniqueId());
    }

    public void addCacheDreamPlayer(Player player, DBDreamPlayer dbDreamPlayer) {
        if (cacheDreamPlayer.containsKey(player.getUniqueId())) return;

        cacheDreamPlayer.put(player.getUniqueId(), dbDreamPlayer);
    }

    public DreamPlayer getDreamPlayer(Player player) {
        if (!dreamPlayerData.containsKey(player.getUniqueId())) return null;

        return dreamPlayerData.get(player.getUniqueId());
    }

    public void addDreamPlayer(Player player, Location oldLocation) throws IOException {
        player.clearActivePotionEffects(); // supprime tout les effets (ex effets des armures dans l'overworld)

        PlayerInventory playerInv = player.getInventory();

        ItemStack[] oldInv = playerInv.getContents().clone();

        DBDreamPlayer cacheDreamPlayer = getCacheDreamPlayer(player);
        if (cacheDreamPlayer == null || cacheDreamPlayer.getDreamInventory() == null) {
            player.getInventory().clear();
        } else {
            BukkitSerializer.playerInventoryFromBase64(playerInv, cacheDreamPlayer.getDreamInventory());
            player.updateInventory();
        }

        PlayerInventory dreamPlayerInv = player.getInventory();
        DreamPlayer newDreamPlayer = new DreamPlayer(player, oldInv, oldLocation, dreamPlayerInv);
        dreamPlayerData.put(player.getUniqueId(), newDreamPlayer);
        playerSaveData.put(player.getUniqueId(), newDreamPlayer.savePlayer());
    }

    public void removeDreamPlayer(Player player, Location dreamLocation) {
        player.closeInventory();
        player.clearActivePotionEffects(); // supprime les effets des armures des reves

        DreamPlayer dreamPlayer = dreamPlayerData.remove(player.getUniqueId());
        playerSaveData.remove(player.getUniqueId());

        if (dreamPlayer == null) {
            OMCLogger.warn("Cannot remove player {}({}) from Dream", player.getName(), player.getUniqueId());
            return;
        }

        dreamPlayer.cancelTimeTask();
        dreamPlayer.cancelColdTask();

        ItemStack[] oldInventory = dreamPlayer.getOldInventory();
        PlayerInventory dreamInventory = player.getInventory();

        DBDreamPlayer cacheDreamPlayer = getCacheDreamPlayer(player);
        String serializedDreamInventory = BukkitSerializer.playerInventoryToBase64(dreamInventory);
        if (cacheDreamPlayer != null) {
            cacheDreamPlayer.setDreamInventory(serializedDreamInventory);
            cacheDreamPlayer.setDreamX(dreamLocation.getX());
            cacheDreamPlayer.setDreamY(dreamLocation.getY());
            cacheDreamPlayer.setDreamZ(dreamLocation.getZ());
        } else {
            addCacheDreamPlayer(player, new DBDreamPlayer(
                    player.getUniqueId(),
                    dreamPlayer.getMaxDreamTime(),
                    serializedDreamInventory,
                    dreamLocation.getX(),
                    dreamLocation.getY(),
                    dreamLocation.getZ(),
                    0
            ));
        }

        player.getInventory().setContents(oldInventory);
        player.updateInventory();
        saveDreamPlayerData(cacheDreamPlayer);
    }

    public void preloadSavePlayer(Player player, Location dreamLocation) throws IOException {
        DBPlayerSave playerSave = playerSaveData.remove(player.getUniqueId());

        if (playerSave == null) {
            player.teleportAsync(SpawnManager.getSpawnLocation());
            OMCLogger.warn("Nothing to load from {}({})", player.getName(), player.getUniqueId());
            return;
        }
        PlayerInventory dreamInventory = player.getInventory();
        String serializedDreamInventory = BukkitSerializer.playerInventoryToBase64(dreamInventory);

        BukkitSerializer.playerInventoryFromBase64(dreamInventory, playerSave.getInventory());
        player.updateInventory();

        DBDreamPlayer cacheDreamPlayer = getCacheDreamPlayer(player);
        if (cacheDreamPlayer != null) {
            cacheDreamPlayer.setDreamInventory(serializedDreamInventory);
            cacheDreamPlayer.setDreamX(dreamLocation.getX());
            cacheDreamPlayer.setDreamY(dreamLocation.getY());
            cacheDreamPlayer.setDreamZ(dreamLocation.getZ());
        } else {
            addCacheDreamPlayer(player, new DBDreamPlayer(
                    player.getUniqueId(),
                    this.BASE_DREAM_TIME,
                    serializedDreamInventory,
                    dreamLocation.getX(),
                    dreamLocation.getY(),
                    dreamLocation.getZ(),
                    0
            ));
            cacheDreamPlayer = getCacheDreamPlayer(player);
        }

        saveDreamPlayerData(cacheDreamPlayer);

        World oldWorld = Bukkit.getWorld(playerSave.getWorld());

        if (oldWorld == null) return;

        player.teleportAsync(
                new Location(
                        oldWorld,
                        playerSave.getX(),
                        playerSave.getY(),
                        playerSave.getZ()
                )
        );
    }

    public void setMaxTime(Player player, long maxTime) {
        DBDreamPlayer cache = this.getCacheDreamPlayer(player);

        if (cache == null) {
            DreamPlayer dreamPlayer = this.getDreamPlayer(player);
            if (dreamPlayer == null) return;

            this.saveDreamPlayerData(dreamPlayer);
            cache = this.getCacheDreamPlayer(player);
            if (cache == null) {
                OMCLogger.warn("player ({}) had no cache even after saving it. [DreamManager#setMaxTime]", player.getUniqueId());
                return;
            }
        }

        cache.setMaxDreamTime(maxTime);
        this.saveDreamPlayerData(cache);
    }

    public double calculateDreamProbability(Player player) {
        double base = 0.15;
        PlayerInventory inv = player.getInventory();

        ItemStack[] armor = {
                inv.getHelmet(),
                inv.getChestplate(),
                inv.getLeggings(),
                inv.getBoots()
        };

        for (ItemStack item : armor) {
            DreamItem dream = OMCRegistry.DREAM_ITEM.getByItemStack(item);

            if (dream != null && dream.getId().contains("omc_dream:pyjama")) {
                base += 0.05;
            }
        }

        City city = City.ofPlayer(player);
        if (city != null && PerkUtils.hasPerk(city.getMayor(), Perks.GREAT_SLEEPER.getId())) {
            base += 0.4;
        }
        return base;
    }

    public void tpPlayerDream(Player player) {
        Biome biome = DreamBiome.SCULK_PLAINS.getBiome();

        if (DreamDimensionManager.DREAM_WORLD == null) return;

        Location spawningLocation = LocationUtils.findLocationInBiome(DreamDimensionManager.DREAM_WORLD, biome);

        if (spawningLocation == null) return;

        player.teleportAsync(spawningLocation);
    }

    public void tpPlayerToLastDreamLocation(Player player) {
        DBDreamPlayer dbDreamPlayer = getCacheDreamPlayer(player);
        if (dbDreamPlayer == null) return;

        player.teleportAsync(new Location(
                DreamDimensionManager.DREAM_WORLD,
                dbDreamPlayer.getDreamX(),
                dbDreamPlayer.getDreamY(),
                dbDreamPlayer.getDreamZ()
        ));
    }

    public void setProgressionOrb(Player player, int progressionOrb, DreamBiome unlocked) {
        DBDreamPlayer cache = this.getCacheDreamPlayer(player);

        if (cache == null) {
            DreamPlayer dreamPlayer = this.getDreamPlayer(player);
            if (dreamPlayer == null) return;

            this.saveDreamPlayerData(dreamPlayer);
            cache = this.getCacheDreamPlayer(player);
            if (cache == null) {
                OMCLogger.warn("player ({}) had no cache even after saving it. [PlayerObtainOrb#setProgressionOrb]", player.getUniqueId());
                return;
            }
        }

        int current = cache.getProgressionOrb();

        if (current >= progressionOrb) return;

        cache.setProgressionOrb(progressionOrb);
        this.saveDreamPlayerData(cache);
        if (unlocked != null)
            sendMessageProgression(player, unlocked);
        sendBroadcastMessageOrb(player, progressionOrb);
    }

    private void sendBroadcastMessageOrb(Player player, int progressionOrb) {
        Component orb = switch (progressionOrb) {
            case 1 -> TranslationManager.translation("feature.dream.item.domination_orb.name");
            case 2 -> TranslationManager.translation("feature.dream.item.ame_orb.name");
            case 3 -> TranslationManager.translation("feature.dream.item.cloud_orb.name");
            case 4 -> TranslationManager.translation("feature.dream.item.mud_orb.name");
            case 5 -> TranslationManager.translation("feature.dream.item.glacite_orb.name");
            default -> TranslationManager.translation("feature.dream.item.unknown_orb.name");
        };

        MessagesManager.broadcastMessage(player.getWorld(), TranslationManager.translation(
                "feature.dream.orb.message.obtained",
                Component.text(player.getName()),
                orb
        ), Prefix.DREAM, MessageType.INFO);
    }

    private void sendMessageProgression(Player player, DreamBiome biome) {
        Component biomeName = switch (biome) {
            case SOUL_FOREST -> TranslationManager.translation("feature.dream.biome.progression.soul_forest");
            case CLOUD_LAND -> TranslationManager.translation("feature.dream.biome.progression.cloud_land");
            case MUD_BEACH -> TranslationManager.translation("feature.dream.biome.progression.mud_beach");
            case GLACITE_GROTTO -> TranslationManager.translation("feature.dream.biome.progression.glacite_grotto");
            default -> TranslationManager.translation("feature.dream.biome.progression.unknown");
        };

        MessagesManager.sendMessage(player, TranslationManager.translation(
                "feature.dream.biome.message.unlocked",
                biomeName
        ), Prefix.DREAM, MessageType.SUCCESS, false);
    }
}

package fr.openmc.core.features.corpse;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadIfEnable;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.corpse.commnads.CorpseCommand;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPC;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.DirectionUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.WorldUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Credit(developers = {"Nocolm"})
public class CorpseManager extends Feature implements LoadIfEnable<FancyNpcsHook>, HasDatabase, HasListeners, HasCommands {

    @Getter
    private static Map<UUID, DBCorpse> corpsesDB;

    private static Dao<DBCorpse, String> corpsesDao;

    public static final Map<UUID, Location> lastSafeLocation = new ConcurrentHashMap<>();

    @Override
    public void init() {
        CorpseNPCManager.init();
        corpsesDB = loadAllCorpses();
        startVoidDetection();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBCorpse.class);
        corpsesDao = DaoManager.createDao(connectionSource, DBCorpse.class);
    }

    @Override
    protected void save() {
        saveAllCorpses();
    }

    private static void saveAllCorpses() {
        try {
            corpsesDao.callBatchTasks(() -> {
                for (DBCorpse player : corpsesDB.values()) {
                    corpsesDao.createOrUpdate(player);
                }
                return null;
            });
        } catch (Exception e) {
            OMCLogger.error("Impossible de sauvegarder les corpses pendant l'arret.", e);
        }
    }

    private static void startVoidDetection() {
        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {

                if (!player.getWorld().getName().equals("world_the_end")) continue;

                Location loc = player.getLocation();

                if (lastSafeLocation.get(player.getUniqueId()).equals(loc)) continue;

                Block blockUnder = loc.clone().subtract(0, 1, 0).getBlock();

                if (blockUnder.getType().isSolid()) {
                    lastSafeLocation.put(player.getUniqueId(), loc.clone());
                }
            }

        }, 0L, 10L);
    }

    public static Map<UUID, DBCorpse> loadAllCorpses() {
        Map<UUID, DBCorpse> corpses = new HashMap<>();

        try {
            List<DBCorpse> dbCorpses = corpsesDao.queryForAll();

            for (DBCorpse corpse : dbCorpses) {
                corpses.put(corpse.getPlayerUUID(), corpse);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return corpses;
    }

    public static boolean createCorpse(Player player, boolean killByPlayer, EntityDamageEvent.DamageCause cause) {

        if (hasCorpseDB(player.getUniqueId())) return false;

        ItemStack[] contents = player.getInventory().getContents().clone();

        // keep 40% of the current experience of the player
        int exp = (int) (player.calculateTotalExperiencePoints() * 0.4);

        corpsesDB.put(player.getUniqueId(), new DBCorpse(
                player.getUniqueId(), contents, player.getLocation(), exp, killByPlayer
        ));

        Location location = player.getLocation();
        Pose pose = Pose.SWIMMING;

        switch (cause) {
            case LAVA, DROWNING -> { // if the corpse is in the water / lava, found air, otherwise let the corpse where the player died
                int iteration = player.getWorld().getMaxHeight() - player.getLocation().getBlockY();
                Location deathLoc = location.clone();
                Location exposedLocation = null;

                for (int y = 0; y < iteration; y++) {
                    deathLoc.add(0, 1, 0);

                    if (deathLoc.getBlock().isLiquid()) continue;

                    if (deathLoc.getBlock().getType().isAir()) {
                        exposedLocation = deathLoc;
                        break;
                    }
                    if (deathLoc.getBlock().isSolid()) break;
                }

                if (exposedLocation != null)
                    location = exposedLocation;
            }
            case VOID -> {
                location = getLastSafePositionOf(player);
                pose = Pose.SITTING;
            }
        }

        return CorpseNPCManager.createNPCS(
                player,
                location,
                player.getEquipment().getHelmet(),
                player.getEquipment().getChestplate(),
                player.getEquipment().getLeggings(),
                player.getEquipment().getBoots(),
                pose,
                killByPlayer
        );
    }

    public static void deleteCorpse(UUID ownerUUID, FoundTypes found) {
        DBCorpse dbCorpse = corpsesDB.remove(ownerUUID);

        ItemStack[] items = dbCorpse.getInventoryContent().clone();
        Location deathLoc = dbCorpse.getLocation().clone();

        try {
            corpsesDao.delete(dbCorpse);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        CorpseNPCManager.removeNPCS(ownerUUID);

        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(ownerUUID);

        switch (found) {
            case FOUND -> {
                DynamicCooldownManager.clear(ownerUUID, CorpseNPCManager.COOLDOWN_GROUP, false);
                MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.found")
                                .color(TextColor.color(Color.GREEN.asRGB())),
                        Prefix.CORPSE, MessageType.SUCCESS, true);
            }

            case NOT_FOUND -> MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.not_found")
                            .color(TextColor.color(Color.YELLOW.asRGB())),
                    Prefix.CORPSE, MessageType.WARNING, true);

            case STRIP -> DynamicCooldownManager.clear(ownerUUID, CorpseNPCManager.COOLDOWN_GROUP, false);

            case ABORT -> {
                for (ItemStack item : items)
                    deathLoc.getWorld().dropItem(deathLoc, item);

                DynamicCooldownManager.clear(ownerUUID, CorpseNPCManager.COOLDOWN_GROUP, false);
                MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.abort")
                                .color(TextColor.color(Color.GREEN.asRGB())),
                        Prefix.CORPSE, MessageType.SUCCESS, true);
            }
        }
    }

    public static void sendMailItems(Player player, OfflinePlayer receiver, ItemStack[] items) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            if (!MailboxManager.sendItems(player, receiver, items))
                MailboxManager.givePlayerItems(player, items);
        });
    }

    public static boolean hasCorpseDB(UUID playerUUID) {
        if (corpsesDB == null) return false;
        return corpsesDB.containsKey(playerUUID);
    }

    public static Location getLastSafePositionOf(Player player) {
        if (!lastSafeLocation.containsKey(player.getUniqueId())) return player.getLocation();
        return lastSafeLocation.get(player.getUniqueId());
    }

    public static Component getCorpseDirection(Player player, CorpseNPC corpse) {

        if (player.getWorld() != corpse.getLocation().getWorld())
            return TranslationManager.translation(WorldUtils.getDisplayedWorldName(corpse.getLocation().getWorld().getName()))
                    .color(TextColor.color(0xFF8F06))
                    .decoration(TextDecoration.BOLD, false);
        else
            return Component.text(
                            DirectionUtils.getDirectionArrow(player, corpse.getLocation()),
                    TextColor.color(0xFF8F06)).decoration(TextDecoration.BOLD, false);
    }

    public static Component getRemainingTime(UUID playerUUID) {

        Component alreadyEnd = TranslationManager.translation("feature.corpse.cooldown.already_end");

        if (DynamicCooldownManager.getCooldowns(playerUUID) == null) return alreadyEnd;
        if (DynamicCooldownManager.getCooldowns(playerUUID).get(CorpseNPCManager.COOLDOWN_GROUP) == null) return alreadyEnd;
        if (DynamicCooldownManager.getCooldowns(playerUUID).get(CorpseNPCManager.COOLDOWN_GROUP).isReady()) return alreadyEnd;
        return Component.text(
                DateUtils.convertMillisToTime(DynamicCooldownManager.getCooldowns(playerUUID)
                        .get(CorpseNPCManager.COOLDOWN_GROUP)
                        .getRemaining()), NamedTextColor.RED).decoration(TextDecoration.BOLD, false);
    }

    public static Component getLocation(Location location) {
        return Component.text("world : " + TranslationManager.translation(WorldUtils.getDisplayedWorldName(location.getWorld().getName()))
                + " x: " + location.getBlockX()
                + " y: " + location.getBlockY()
                + " z: " + location.getBlockZ()
        );
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(CorpseListener::new);
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new CorpseCommand()
        );
    }
}

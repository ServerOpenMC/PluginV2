package fr.openmc.core.features.corpse;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

@Credit(developers = {"Nocolm"})
public class CorpseManager extends Feature implements HasDatabase, HasListeners {

    @Getter
    private static Map<UUID, DBCorpse> corpsesDB;

    private static Dao<DBCorpse, String> corpsesDao;

    @Override
    public void init() {
        CorpseNPCManager.init();
        corpsesDB = loadAllCorpses();
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
                        Prefix.OPENMC, MessageType.SUCCESS, true);
            }

            case NOT_FOUND -> MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.not_found")
                            .color(TextColor.color(Color.YELLOW.asRGB())),
                    Prefix.OPENMC, MessageType.WARNING, true);

            case STRIP -> DynamicCooldownManager.clear(ownerUUID, CorpseNPCManager.COOLDOWN_GROUP, false);
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
        if (!CorpseListener.lastSafeLocation.containsKey(player.getUniqueId())) return player.getLocation();
        return CorpseListener.lastSafeLocation.get(player.getUniqueId());
    }

    public static Component getRemainingTime(UUID playerUUID) {

        Component alreadyEnd = TranslationManager.translation("feature.corpse.cooldown.already_end");

        if (DynamicCooldownManager.getCooldowns(playerUUID) == null) return alreadyEnd;
        if (DynamicCooldownManager.getCooldowns(playerUUID).get(CorpseNPCManager.COOLDOWN_GROUP) == null) return alreadyEnd;
        if (DynamicCooldownManager.getCooldowns(playerUUID).get(CorpseNPCManager.COOLDOWN_GROUP).isReady()) return alreadyEnd;
        return Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getCooldowns(playerUUID).get(CorpseNPCManager.COOLDOWN_GROUP).getRemaining()));
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new CorpseListener()
        );
    }
}

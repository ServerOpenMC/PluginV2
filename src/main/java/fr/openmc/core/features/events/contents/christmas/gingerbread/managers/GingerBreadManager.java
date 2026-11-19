package fr.openmc.core.features.events.contents.christmas.gingerbread.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.christmas.gingerbread.commands.GingerbreadCommands;
import fr.openmc.core.features.events.contents.christmas.gingerbread.listeners.GingerbreadNPCListener;
import fr.openmc.core.features.events.contents.christmas.gingerbread.models.GingerbreadData;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.utils.cache.CachePlayerName;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.tag.Tag;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class GingerBreadManager extends Feature implements HasDatabase, HasCommands, HasListeners {
    private static Object2ObjectMap<UUID, GingerbreadData> gingerbreadData;
    private static Dao<GingerbreadData, String> gingerbreadDataDao;

    public void init() {
       gingerbreadData = loadAllgingerbreadData();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new GingerbreadCommands()
        );
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(GingerbreadNPCListener::new);
    }

    public static void depositGingerbread(UUID playerUUID, int amount) {
        GingerbreadData data = gingerbreadData.get(playerUUID);
        data.depositPumpkins(amount);
        gingerbreadData.put(playerUUID, data);
        new BukkitRunnable() {
            @Override
            public void run() {
                saveGingerbreadData(data);
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    public static int getGingerbreadCount(UUID playerUUID) {
        GingerbreadData data = gingerbreadData.computeIfAbsent(playerUUID, GingerbreadData::new);
        return data.getGingerbreadCount();
    }

    public static Object2ObjectMap<UUID, GingerbreadData> getAllGingerbreadData() {
        return gingerbreadData;
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, GingerbreadData.class);
        gingerbreadDataDao = DaoManager.createDao(connectionSource, GingerbreadData.class);
    }

    private static void saveGingerbreadData(GingerbreadData data) {
        try {
            gingerbreadDataDao.createOrUpdate(data);
        } catch (SQLException e) {
            OMCLogger.error("Failed to save gingerbread data {}", data.getPlayerUUID(), e);
        }
    }

    private static Object2ObjectMap<UUID, GingerbreadData> loadAllgingerbreadData() {
        Object2ObjectMap<UUID, GingerbreadData> newGingerbreadData = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        try {
            List<GingerbreadData> gingerbreadDataDBs = gingerbreadDataDao.queryForAll();
            for (GingerbreadData gingerbreadData : gingerbreadDataDBs) {
                newGingerbreadData.put(gingerbreadData.getPlayerUUID(), gingerbreadData);
            }
        } catch (SQLException e) {
            OMCLogger.error("Failed to load gingerbread datas from database", e);
        }

        return newGingerbreadData;
    }

    public static void endEvent() {
        Map<OfflinePlayer, ItemStack[]> playerItemsMap = new HashMap<>();

        Map<Integer, Map.Entry<String, String>> newMap = new TreeMap<>();
        int rank = 1;

        Object2ObjectMap<UUID, GingerbreadData> balances = GingerBreadManager.getAllGingerbreadData();
        for (var entry : balances.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue().getGingerbreadCount(), entry1.getValue().getGingerbreadCount()))
                .limit(10)
                .toList()) {
            String playerName = CachePlayerName.getName(entry.getKey());
            String formattedGingerbreadCount = EconomyManager.getFormattedSimplifiedNumber(entry.getValue().getGingerbreadCount());
            newMap.put(rank++, new AbstractMap.SimpleEntry<>(playerName, formattedGingerbreadCount));
        }

        for (Map.Entry<Integer, Map.Entry<String, String>> entries : newMap.entrySet()) {
            rank = entries.getKey();
            String playerName = entries.getValue().getKey();
            String gingerbreadCount = entries.getValue().getValue();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

            List<ItemStack> rewards = new ArrayList<>();

            final ItemStack aywenite = OMCRegistry.CUSTOM_ITEMS.AYWENITE.getBest();
            aywenite.setAmount(64);
            switch (rank) {
                case 1 -> {
                    ItemStack customGingerbread = ItemStack.of(Material.PUMPKIN_PIE);
                    customGingerbread.unsetData(DataComponentTypes.CONSUMABLE);
                    customGingerbread.unsetData(DataComponentTypes.FOOD);


                    Registry<DamageType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
                    Tag<DamageType> fireTag = registry.getTag(DamageTypeTagKeys.IS_FIRE);

                    customGingerbread.setData(
                            DataComponentTypes.DAMAGE_RESISTANT,
                            DamageResistant.damageResistant(fireTag)
                    );

                    customGingerbread.editMeta(meta -> {
                        Component ownerName = offlinePlayer.getName() == null ? TranslationManager.translation("feature.events.gingerbread.unknown_player") : Component.text(offlinePlayer.getName());
                        ownerName = ownerName.color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
                        meta.itemName(TranslationManager.translation("feature.events.gingerbread.reward.rank1.name"));
                        meta.lore(TranslationManager.translationLore("feature.events.gingerbread.reward.rank1.lore", ownerName));
                    });

                    rewards.addAll(List.of(customGingerbread, aywenite, aywenite.clone(), aywenite.clone()));
                    EconomyManager.addBalance(offlinePlayer.getUniqueId(), 30000);
                }

                case 2 -> {
                    ItemStack customPumpkin = ItemStack.of(Material.PUMPKIN_PIE);
                    customPumpkin.unsetData(DataComponentTypes.CONSUMABLE);
                    customPumpkin.unsetData(DataComponentTypes.FOOD);

                    Registry<DamageType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
                    Tag<DamageType> fireTag = registry.getTag(DamageTypeTagKeys.IS_FIRE);

                    customPumpkin.setData(
                            DataComponentTypes.DAMAGE_RESISTANT,
                            DamageResistant.damageResistant(fireTag)
                    );

                    customPumpkin.editMeta(meta -> {
                        Component ownerName = offlinePlayer.getName() == null ? TranslationManager.translation("feature.events.gingerbread.unknown_player") : Component.text(offlinePlayer.getName());
                        ownerName = ownerName.color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
                        meta.itemName(TranslationManager.translation("feature.events.gingerbread.reward.rank2.name"));
                        meta.lore(TranslationManager.translationLore("feature.events.gingerbread.reward.rank2.lore", ownerName));
                    });

                    rewards.addAll(List.of(customPumpkin, aywenite, aywenite.clone()));
                    EconomyManager.addBalance(offlinePlayer.getUniqueId(), 20000);
                }

                case 3 -> {
                    ItemStack customPumpkin = ItemStack.of(Material.PUMPKIN_PIE);
                    customPumpkin.unsetData(DataComponentTypes.CONSUMABLE);
                    customPumpkin.unsetData(DataComponentTypes.FOOD);

                    Registry<DamageType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
                    Tag<DamageType> fireTag = registry.getTag(DamageTypeTagKeys.IS_FIRE);

                    customPumpkin.setData(
                            DataComponentTypes.DAMAGE_RESISTANT,
                            DamageResistant.damageResistant(fireTag)
                    );

                    customPumpkin.editMeta(meta -> {
                        Component ownerName = offlinePlayer.getName() == null ? TranslationManager.translation("feature.events.gingerbread.unknown_player") : Component.text(offlinePlayer.getName());
                        ownerName = ownerName.color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
                        meta.itemName(TranslationManager.translation("feature.events.gingerbread.reward.rank3.name"));
                        meta.lore(TranslationManager.translationLore("feature.events.gingerbread.reward.rank3.lore", ownerName));
                    });

                    rewards.addAll(List.of(customPumpkin, aywenite));
                    EconomyManager.addBalance(offlinePlayer.getUniqueId(), 10000);
                }

                default -> {
                    if (!gingerbreadCount.equals("0"))
                        EconomyManager.addBalance(offlinePlayer.getUniqueId(), 3000);
                }
            }

            playerItemsMap.put(offlinePlayer, rewards.toArray(new ItemStack[0]));
        }

        MailboxManager.sendItemsToAOfflinePlayerBatch(playerItemsMap);

        NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
        Npc gingerbreadNPC = npcManager.getNpc("gingerbread_pumpkin_deposit_npc");
        gingerbreadNPC.removeForAll();
        npcManager.removeNpc(gingerbreadNPC);

        Bukkit.getServer().sendMessage(TranslationManager.translation("feature.events.gingerbread.event.end.broadcast"));
    }
}

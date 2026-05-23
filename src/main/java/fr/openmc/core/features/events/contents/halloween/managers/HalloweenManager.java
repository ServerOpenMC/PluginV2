package fr.openmc.core.features.events.contents.halloween.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.commands.HalloweenCommands;
import fr.openmc.core.features.events.contents.halloween.listeners.HalloweenNPCListener;
import fr.openmc.core.features.events.contents.halloween.models.HalloweenData;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class HalloweenManager extends Feature implements DatabaseFeature, HasCommands, HasListeners {
    private static Object2ObjectMap<UUID, HalloweenData> halloweenData;
    private static Dao<HalloweenData, String> halloweenDataDao;

    public void init() {
       halloweenData = loadAllHalloweenDatas();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new HalloweenCommands()
        );
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new HalloweenNPCListener()
        );
    }

    public static void depositPumpkins(UUID playerUUID, int amount) {
        HalloweenData data = halloweenData.get(playerUUID);
        data.depositPumpkins(amount);
        halloweenData.put(playerUUID, data);
        new BukkitRunnable() {
            @Override
            public void run() {
                saveHalloweenData(data);
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    public static int getPumpkinCount(UUID playerUUID) {
        HalloweenData data = halloweenData.computeIfAbsent(playerUUID, HalloweenData::new);
        return data.getPumpkinCount();
    }

    public static Object2ObjectMap<UUID, HalloweenData> getAllHalloweenData() {
        return halloweenData;
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, HalloweenData.class);
        halloweenDataDao = DaoManager.createDao(connectionSource, HalloweenData.class);
    }

    private static boolean saveHalloweenData(HalloweenData data) {
        try {
            halloweenDataDao.createOrUpdate(data);
            return true;
        } catch (SQLException e) {
            OMCLogger.error("Failed to save halloween data {}", data.getPlayerUUID(), e);
            return false;
        }
    }

    private static Object2ObjectMap<UUID, HalloweenData> loadAllHalloweenDatas() {
        Object2ObjectMap<UUID, HalloweenData> newHalloweenDatas = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        try {
            List<HalloweenData> halloweenDataDBs = halloweenDataDao.queryForAll();
            for (HalloweenData halloweenData : halloweenDataDBs) {
                newHalloweenDatas.put(halloweenData.getPlayerUUID(), halloweenData);
            }
        } catch (SQLException e) {
            OMCLogger.error("Failed to load halloween datas from database", e);
        }

        return newHalloweenDatas;
    }

    public static void endEvent() {
        LeaderboardManager.updatePumpkinCountMap();
        Map<OfflinePlayer, ItemStack[]> playerItemsMap = new HashMap<>();

        for (Map.Entry<Integer, Map.Entry<String, String>> entries : LeaderboardManager.getPumpkinCountMap().entrySet()) {
            int rank = entries.getKey();
            String playerName = entries.getValue().getKey();
            String pumpkinCount = entries.getValue().getValue();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

            List<ItemStack> rewards = new ArrayList<>();

            final ItemStack aywenite = Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest();
            aywenite.setAmount(64);
            switch (rank) {
                case 1 -> {
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
                        Component ownerName = Component.text(offlinePlayer.getName() == null ? TranslationManager.translationString("feature.events.halloween.unknown_player") : offlinePlayer.getName()).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
                        meta.itemName(TranslationManager.translation("feature.events.halloween.reward.rank1.name"));
                        meta.lore(TranslationManager.translationLore("feature.events.halloween.reward.rank1.lore", ownerName));
                    });

                    rewards.addAll(List.of(customPumpkin, aywenite, aywenite.clone(), aywenite.clone()));
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
                        Component ownerName = Component.text(offlinePlayer.getName() == null ? TranslationManager.translationString("feature.events.halloween.unknown_player") : offlinePlayer.getName()).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
                        meta.itemName(TranslationManager.translation("feature.events.halloween.reward.rank2.name"));
                        meta.lore(TranslationManager.translationLore("feature.events.halloween.reward.rank2.lore", ownerName));
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
                        Component ownerName = Component.text(offlinePlayer.getName() == null ? TranslationManager.translationString("feature.events.halloween.unknown_player") : offlinePlayer.getName()).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
                        meta.itemName(TranslationManager.translation("feature.events.halloween.reward.rank3.name"));
                        meta.lore(TranslationManager.translationLore("feature.events.halloween.reward.rank3.lore", ownerName));
                    });

                    rewards.addAll(List.of(customPumpkin, aywenite));
                    EconomyManager.addBalance(offlinePlayer.getUniqueId(), 10000);
                }

                default -> {
                    if (!pumpkinCount.equals("0"))
                        EconomyManager.addBalance(offlinePlayer.getUniqueId(), 3000);
                }
            }

            playerItemsMap.put(offlinePlayer, rewards.toArray(new ItemStack[0]));
        }

        MailboxManager.sendItemsToAOfflinePlayerBatch(playerItemsMap);

        NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
        Npc halloweenNPC = npcManager.getNpc("halloween_pumpkin_deposit_npc");
        halloweenNPC.removeForAll();
        npcManager.removeNpc(halloweenNPC);

        Bukkit.getServer().sendMessage(TranslationManager.translation("feature.events.halloween.event.end.broadcast"));
    }
}

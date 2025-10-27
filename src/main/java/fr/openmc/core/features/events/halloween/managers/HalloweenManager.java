package fr.openmc.core.features.events.halloween.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.api.hooks.FancyNpcsHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.halloween.listeners.HalloweenNPCListener;
import fr.openmc.core.features.events.halloween.models.HalloweenData;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.items.CustomItemRegistry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class HalloweenManager {
    private static Object2ObjectMap<UUID, HalloweenData> halloweenData;
    private static Dao<HalloweenData, String> halloweenDataDao;

    public static void init() {
        if (FancyNpcsHook.isHasFancyNpc())
            Bukkit.getPluginManager().registerEvents(new HalloweenNPCListener(), OMCPlugin.getInstance());

        halloweenData = loadAllHalloweenDatas();
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

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, HalloweenData.class);
        halloweenDataDao = DaoManager.createDao(connectionSource, HalloweenData.class);
    }

    private static boolean saveHalloweenData(HalloweenData data) {
        try {
            halloweenDataDao.createOrUpdate(data);
            return true;
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to save halloween data {}", data.getPlayerUUID(), e);
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
            throw new RuntimeException(e);
        }

        return newHalloweenDatas;
    }

    public static void endEvent() {
        LeaderboardManager.updatePumpkinCountMap();
        Map<OfflinePlayer, ItemStack[]> playerItemsMap = new HashMap<>();

        for (Map.Entry<Integer, Map.Entry<String, String>> entries : LeaderboardManager.getPumpkinCountMap().entrySet()) {
            int rank = entries.getKey();
            String playerName = entries.getValue().getKey();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

            List<ItemStack> rewards = new ArrayList<>();

            final ItemStack aywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
            aywenite.setAmount(64);
            switch (rank) {
                case 1 -> {
                    ItemStack customPumpkin = ItemStack.of(Material.PUMPKIN_PIE);
                    customPumpkin.unsetData(DataComponentTypes.CONSUMABLE);
                    customPumpkin.unsetData(DataComponentTypes.FOOD);

                    customPumpkin.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
                    customPumpkin.editMeta(meta -> {
                        meta.itemName(Component.text("La Tarte de la Victoire (2025)", TextColor.color(255, 107, 37), TextDecoration.BOLD, TextDecoration.UNDERLINED));
                        meta.lore(List.of(
                                Component.text("Récompense du joueur ayant récolté le plus de citrouilles", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                                Component.text("lors de l'événement Halloween 2025.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                        ));
                    });

                    rewards.addAll(List.of(customPumpkin, aywenite, aywenite.clone(), aywenite.clone()));
                    EconomyManager.addBalance(offlinePlayer.getUniqueId(), 20000);
                }

                case 2 -> {
                    ItemStack customPumpkin = ItemStack.of(Material.PUMPKIN_PIE);
                    customPumpkin.unsetData(DataComponentTypes.CONSUMABLE);
                    customPumpkin.unsetData(DataComponentTypes.FOOD);

                    customPumpkin.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
                    customPumpkin.editMeta(meta -> {
                        meta.itemName(Component.text("La Tarte de l'Excellence (2025)", TextColor.color(255, 107, 37), TextDecoration.BOLD, TextDecoration.UNDERLINED));
                        meta.lore(List.of(
                                Component.text("Récompense du joueur ayant récolté la deuxième plus", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                                Component.text("grande quantité de citrouilles lors de l'événement", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                                Component.text("Halloween 2025.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                        ));
                    });

                    rewards.addAll(List.of(customPumpkin, aywenite, aywenite.clone()));
                    EconomyManager.addBalance(offlinePlayer.getUniqueId(), 10000);
                }

                case 3 -> {
                    ItemStack customPumpkin = ItemStack.of(Material.PUMPKIN_PIE);
                    customPumpkin.unsetData(DataComponentTypes.CONSUMABLE);
                    customPumpkin.unsetData(DataComponentTypes.FOOD);

                    customPumpkin.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
                    customPumpkin.editMeta(meta -> {
                        meta.itemName(Component.text("La Tarte du Mérite (2025)", TextColor.color(255, 107, 37), TextDecoration.BOLD, TextDecoration.UNDERLINED));
                        meta.lore(List.of(
                                Component.text("Récompense du joueur ayant récolté la troisième plus", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                                Component.text("grande quantité de citrouilles lors de l'événement", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                                Component.text("Halloween 2025.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                        ));
                    });

                    rewards.addAll(List.of(customPumpkin, aywenite));
                    EconomyManager.addBalance(offlinePlayer.getUniqueId(), 5000);
                }

                default -> {
                    ItemStack customPumpkin = ItemStack.of(Material.PUMPKIN_PIE);
                    customPumpkin.unsetData(DataComponentTypes.CONSUMABLE);
                    customPumpkin.unsetData(DataComponentTypes.FOOD);

                    customPumpkin.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
                    customPumpkin.editMeta(meta -> {
                        meta.itemName(Component.text("La Tarte de Participation (2025)", TextColor.color(255, 107, 37), TextDecoration.BOLD, TextDecoration.UNDERLINED));
                        meta.lore(List.of(
                                Component.text("Récompense du joueur ayant participé à l'événement", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                                Component.text("Halloween 2025.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                        ));
                    });

                    rewards.add(customPumpkin);
                    EconomyManager.addBalance(offlinePlayer.getUniqueId(), 100);
                }
            }

            playerItemsMap.put(offlinePlayer, rewards.toArray(new ItemStack[0]));
        }

        MailboxManager.sendItemsToAOfflinePlayerBatch(playerItemsMap);

        NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
        Npc halloweenNPC = npcManager.getNpc("halloween_pumpkin_deposit_npc");
        halloweenNPC.removeForAll();
        npcManager.removeNpc(halloweenNPC);

        Bukkit.getServer().sendMessage(
                    Component.newline()
                    .append(Component.text("L'événement Halloween 2025 est maintenant terminé !", TextColor.color(255, 107, 37))
                            .append(Component.newline())
                            .append(Component.text("Vous pouvez retrouver vos récompenses dans votre", TextColor.color(255, 107, 37))))
                            .append(Component.text(" boîte aux lettres", TextColor.color(255, 107, 37), TextDecoration.BOLD))
                            .append(Component.text(".", TextColor.color(255, 107, 37)))
                            .append(Component.newline())
                            .append(Component.text("Merci à tous pour votre participation !", TextColor.fromHexString("#FFD580")))
                            .append(Component.newline())

        );
    }
}

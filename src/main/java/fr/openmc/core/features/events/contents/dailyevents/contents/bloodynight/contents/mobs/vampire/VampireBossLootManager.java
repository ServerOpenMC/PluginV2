package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class VampireBossLootManager {

    public static final Map<UUID, Double> damageContributions = new HashMap<>();
    private static final CustomLootTable VAMPIRE_BOSS_LOOT_TABLE = OMCRegistry.CUSTOM_LOOT_TABLES.VAMPIRE;

    public static void giveContributions(CustomMob<?> mob) {
        double totalHealth = mob.getHealth();

        Map<UUID, Double> orderedMap = damageContributions.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Double.compare(
                        entry2.getValue(),
                        entry1.getValue()
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));


        World world = DailyEventsManager.BLOODY_NIGHT.getWorld();
        if (world == null) return;

        if (orderedMap.isEmpty()) {
            broadcastToWorld(world, TranslationManager.translation(
                    "feature.dailyevents.bloody_night.vampire_boss.defeated.no_body"
            ));
            return;
        }

        int rankInt = 0;


        broadcastToWorld(world, TranslationManager.translation(
                "feature.dailyevents.bloody_night.vampire_boss.defeated.start"
        ));

        for (Map.Entry<UUID, Double> entry : orderedMap.entrySet()) {
            if (rankInt == 1 || rankInt == 2 || rankInt == 3) {
                broadcastToWorld(world, TranslationManager.translation(
                        "feature.dailyevents.bloody_night.vampire_boss.defeated.rank",
                        Component.text(rankInt + 1, LeaderboardManager.getRankColor(rankInt + 1)),
                        PlayerNameCache.name(entry.getKey()).color(NamedTextColor.GOLD),
                        Component.text(String.format("%.1f", entry.getValue()), NamedTextColor.RED)
                ));
            } else {
                OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(entry.getKey());
                if (offlinePlayer == null) continue;
                if (!offlinePlayer.isOnline()) continue;
                Player player = offlinePlayer.getPlayer();
                if (player == null) continue;

                player.sendMessage(TranslationManager.translation(
                        "feature.dailyevents.bloody_night.vampire_boss.defeated.midle",
                        Component.text(rankInt + 1, LeaderboardManager.getRankColor(rankInt + 1)),
                        PlayerNameCache.name(entry.getKey()).color(NamedTextColor.GOLD),
                        Component.text(String.format("%.1f", entry.getValue()), NamedTextColor.RED)
                ));
            }

            UUID playerUUID = entry.getKey();
            double damage = entry.getValue();
            double participation = damage / totalHealth;

            if (participation < 0.01) continue;

            double chanceMultiplier = getChanceMultiplier(participation);
            giveLootToContributor(playerUUID, chanceMultiplier);

            rankInt++;
        }

        broadcastToWorld(world, TranslationManager.translation(
                "feature.dailyevents.bloody_night.vampire_boss.defeated.end"));
    }

    private static void giveLootToContributor(UUID playerUUID, double chanceMultiplier) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);

        Player player = offlinePlayer.getPlayer();


        for (CustomLoot loot : VAMPIRE_BOSS_LOOT_TABLE.getLoots()) {
            if (!(loot instanceof ItemLoot) && !(loot instanceof MoneyLoot)) continue;

            double finalChance = loot.getChance() * chanceMultiplier;

            if (finalChance > 1.0) {
                finalChance = 1.0;
            } else if (finalChance < 0.0) {
                finalChance = 0.0;
            }

            if (random.nextDouble() > finalChance) continue;

            int amount = -1;
            if (loot instanceof ItemLoot itemLoot) {
                amount = itemLoot.getRepresentativeItem().getAmount();
                MailboxManager.sendItemsToOfflinePlayer(offlinePlayer, itemLoot.getItems().toArray(new ItemStack[0]));
            } else if (loot instanceof MoneyLoot moneyLoot) {
                EconomyManager.addBalance(playerUUID, moneyLoot.getMoney());
            }

            if (player != null && offlinePlayer.isOnline()) {
                loot.sendLootMessage(player, amount);
            }
        }
    }

    private static double getChanceMultiplier(double participation) {
        if (participation >= 0.75) {
            return 1.0;
        } else if (participation >= 0.50) {
            return 0.80;
        } else if (participation >= 0.25) {
            return 0.60;
        } else if (participation >= 0.10) {
            return 0.40;
        } else {
            return 0.25;
        }
    }

    private static void broadcastToWorld(World world, Component component) {
        for (Player player : world.getPlayers()) {
            player.sendMessage(component);
        }
    }
}

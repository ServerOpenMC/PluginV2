package fr.openmc.core.features.city.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityClaimCondition;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.hooks.WorldGuardHook;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.chunk.ChunkPos;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class CityClaimAction {
    private static final int[][] CARDINAL_OFFSETS = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

    public static int calculatePrice(int chunkCount) {
        double maxValue = 40000;
        double k = 0.015;

        double value = maxValue * (1 - Math.exp(-k * chunkCount));
        return (int) (2000 + value);
    }

    public static int calculateAywenite(int chunkCount) {
        return chunkCount;
    }

    public static void startClaim(Player sender, int chunkX, int chunkZ) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        org.bukkit.World bWorld = sender.getWorld();
        if (!bWorld.getName().equals("world")) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.claim.cant_claim_here"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!CityClaimCondition.canCityClaim(city, sender)) return;

        ChunkPos chunkVec2 = new ChunkPos(chunkX, chunkZ);

        if (!isAdjacentToOwnCity(chunkVec2, city.getChunks())) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.claim.isnt_adjacent"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Chunk chunk = sender.getWorld().getChunkAt(chunkX, chunkZ);
        if (WorldGuardHook.doesChunkContainWGRegion(chunk)) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.claim.is_in_region"),
                    Prefix.CITY, MessageType.ERROR, true);
            return;
        }

        if (CityManager.isChunkClaimed(chunkX, chunkZ)) {
            City chunkCity = CityManager.getCityFromChunk(chunkX, chunkZ);
            if (chunkCity == null) return;
            String cityName = chunkCity.getName();
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.claim.already_claim",
                    Component.text(cityName)), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int price = calculatePrice(city.getChunks().size());
        int aywenite = calculateAywenite(city.getChunks().size());

        if (city.getFreeClaims() <= 0) {
            if (city.getBalance() < price) {
                MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.city_not_enough_money",
                                Component.text(price + EconomyManager.getEconomyIcon())),
                        Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (ItemUtils.takeAywenite(sender, aywenite))
                city.updateBalance(price * -1);
        } else {
            city.updateFreeClaims(-1);
        }

        city.addChunk(chunkX, chunkZ);

        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.claim.claim_success"),
                Prefix.CITY, MessageType.SUCCESS, false);
    }

    private static boolean isAdjacentToOwnCity(@NotNull ChunkPos newClaim, Set<ChunkPos> cityClaims) {
        for (int[] offset : CARDINAL_OFFSETS) {
            ChunkPos adjacentClaim = new ChunkPos(
                    newClaim.x() + offset[0],
                    newClaim.z() + offset[1]
            );

            if (cityClaims.contains(adjacentClaim))
                return true;
        }

        return false;
    }
}

package fr.openmc.core.features.city.actions;

import fr.openmc.api.hooks.WorldGuardHook;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityClaimCondition;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ChunkPos;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class CityClaimAction {
    private static final int[][] CARDINAL_OFFSETS = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

    public static int calculatePrice(int chunkCount) {
        return 5000 + (chunkCount * 750);
    }

    public static int calculateAywenite(int chunkCount) {
        return chunkCount;
    }

    public static void startClaim(Player sender, int chunkX, int chunkZ) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        org.bukkit.World bWorld = sender.getWorld();
        if (!bWorld.getName().equals("world")) {
            MessagesManager.sendMessage(sender, Component.text("Tu ne peux pas étendre ta ville ici"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!CityClaimCondition.canCityClaim(city, sender)) return;

        ChunkPos chunkVec2 = new ChunkPos(chunkX, chunkZ);

        if (!isAdjacentToOwnCity(chunkVec2, city.getChunks())) {
            MessagesManager.sendMessage(sender, Component.text("Ce chunk n'est pas adjacent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Chunk chunk = sender.getWorld().getChunkAt(chunkX, chunkZ);
        if (WorldGuardHook.doesChunkContainWGRegion(chunk)) {
            MessagesManager.sendMessage(sender, Component.text("Ce chunk est dans une région protégée"), Prefix.CITY, MessageType.ERROR, true);
            return;
        }

        if (CityManager.isChunkClaimed(chunkX, chunkZ)) {
            City chunkCity = CityManager.getCityFromChunk(chunkX, chunkZ);
            String cityName = chunkCity.getName();
            MessagesManager.sendMessage(sender, Component.text("Ce chunk est déjà claim par " + cityName + "."), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int price = calculatePrice(city.getChunks().size());
        int aywenite = calculateAywenite(city.getChunks().size());

        if (city.getFreeClaims() <= 0) {
            if (city.getBalance() < price) {
                MessagesManager.sendMessage(sender, Component.text("Ta ville n'a pas assez d'argent (" + price + EconomyManager.getEconomyIcon() + " nécessaires)"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (ItemUtils.takeAywenite(sender, aywenite))
                city.updateBalance(price * -1);
        } else {
            city.updateFreeClaims(-1);
        }

        city.addChunk(chunkX, chunkZ);

        MessagesManager.sendMessage(sender, Component.text("Ta ville a été étendue"), Prefix.CITY, MessageType.SUCCESS, false);
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

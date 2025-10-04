package fr.openmc.core.features.cosmetics.bodycostmetics;

import fr.openmc.core.utils.PlayerBodyUtils;
import fr.openmc.core.utils.entities.BodyCosmeticItemDisplay;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("unused")
public class BodyCosmeticsManager {
    private final Map<UUID, BodyCosmeticItemDisplay> itemDisplays;
    private final Map<UUID, Float> bodyYawMap;
    private final Map<UUID, Position> lastPositionMap;

    public BodyCosmeticsManager(JavaPlugin plugin) {
        itemDisplays = new HashMap<>();
        bodyYawMap = new HashMap<>();
        lastPositionMap = new HashMap<>();
        int[] i = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                itemDisplays.forEach((uuid, bodyCosmeticItemsDisplays) -> {
                    updatePlayerBodyYaw(uuid);
                    if (i[0] % 20 == 0) { // Every 20 ticks
                        updatePlayerBodyCosmeticsViewerList(uuid);
                    }
                });
                i[0]++;
            }
        }.runTaskTimerAsynchronously(plugin, 1L, 1L);
    }

    public void spawnPlayerBodyCosmetic(ServerPlayer player, BodyCosmetic cosmetic) {
        UUID playerUUID = player.getUUID();
        removePlayerBodyCosmetic(playerUUID);

        updatePlayerBodyYaw(playerUUID);

        BodyCosmeticItemDisplay itemDisplay = new BodyCosmeticItemDisplay(player, cosmetic);
        itemDisplays.put(playerUUID, itemDisplay);
    }

    public void updatePlayerBodyCosmeticsTransformations(UUID uuid) {
        if (!itemDisplays.containsKey(uuid)) return;
        itemDisplays.get(uuid).updateTransformation();
    }

    public void updatePlayerBodyCosmeticsViewerList(UUID uuid) {
        if (!itemDisplays.containsKey(uuid)) return;
        itemDisplays.get(uuid).updateViewersList();
    }

    public void updatePlayerBodyYaw(UUID uuid) {
        ServerPlayer player = MinecraftServer.getServer().getPlayerList().getPlayer(uuid);
        if (player == null) return;
        Float lastBodyYaw = bodyYawMap.getOrDefault(uuid, player.getYRot());
        Entity vehicle = player.getVehicle();
        Position lastPosition = lastPositionMap.getOrDefault(uuid, new Position((float) player.getX(), (float) player.getZ()));
        if (vehicle != null) {
            if (vehicle.isClientAuthoritative()) {
                lastBodyYaw = vehicle.getYRot();
            } else {
                lastBodyYaw = PlayerBodyUtils.getBodyYaw(
                        player.attackAnim,
                        player.getYRot(),
                        0,
                        0,
                        0,
                        0,
                        vehicle.getYRot()
                );
            }
        } else {
            lastBodyYaw = PlayerBodyUtils.getBodyYaw(
                    player.attackAnim,
                    player.getYRot(),
                    (float) player.getX(),
                    (float) player.getZ(),
                    lastPosition.x,
                    lastPosition.z,
                    lastBodyYaw
            );
        }
        bodyYawMap.put(uuid, lastBodyYaw);
        lastPositionMap.put(uuid, new Position((float) player.getX(), (float) player.getZ()));
        updatePlayerBodyCosmeticsTransformations(uuid);
    }

    private record Position(float x, float z) {
    }

    public float getPlayerBodyYaw(UUID uuid) {
        return bodyYawMap.getOrDefault(uuid, 0f);
    }

    public void removePlayerBodyCosmetic(UUID uuid) {
        if (!itemDisplays.containsKey(uuid)) return;
        itemDisplays.get(uuid).remove();
        itemDisplays.remove(uuid);
        bodyYawMap.remove(uuid);
    }

}

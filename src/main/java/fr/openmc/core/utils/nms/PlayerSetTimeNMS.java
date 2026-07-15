package fr.openmc.core.utils.nms;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.WorldClock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerSetTimeNMS {

    /**
     * Envoie un packet au joueur pour définir l'heure du monde
     * @param player le joueur a qui envoyé le packet
     * @param time le temps envoyé
     */
    public static void sendPacketSetTime(Player player, int time) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        Registry<WorldClock> clockRegistry = MinecraftServer.getServer()
                .registryAccess()
                .lookupOrThrow(Registries.WORLD_CLOCK);

        Map<Holder<WorldClock>, ClockNetworkState> clockUpdates = new HashMap<>();

        clockRegistry.listElements().forEach(clockHolder -> {
            ClockNetworkState state = new ClockNetworkState(time, 0.0F, 0.0F);
            clockUpdates.put(clockHolder, state);
        });

        long gameTime = serverPlayer.level().getGameTime();
        ClientboundSetTimePacket packet = new ClientboundSetTimePacket(gameTime, clockUpdates);

        serverPlayer.connection.send(packet);
    }
}

package fr.openmc.core.utils.nms;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerWeatherNMS {
    public static void setWeather(Player player, WeatherType type) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        switch (type) {
            case NONE -> {
                    send(nmsPlayer, ClientboundGameEventPacket.STOP_RAINING, 0.0f);
                    send(nmsPlayer, ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 0.0f);
                    send(nmsPlayer, ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, 0.0f);
            }
            case RAIN -> {
                send(nmsPlayer, ClientboundGameEventPacket.START_RAINING, 0.0f);
                send(nmsPlayer, ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 1.0f);
                send(nmsPlayer, ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, 0.0f);
            }
            case STORM -> {
                send(nmsPlayer, ClientboundGameEventPacket.START_RAINING, 0.0f);
                send(nmsPlayer, ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 1.0f);
                send(nmsPlayer, ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, 1.0f);
            }
        }
    }

    private static void send(ServerPlayer nmsPlayer, ClientboundGameEventPacket.Type event, float value) {
        nmsPlayer.connection.send(new ClientboundGameEventPacket(event, value));
    }
}

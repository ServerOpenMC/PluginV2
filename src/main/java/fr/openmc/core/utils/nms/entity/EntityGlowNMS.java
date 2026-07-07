package fr.openmc.core.utils.nms.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class EntityGlowNMS implements Listener {
    private static final Scoreboard NMS_SCOREBOARD = new Scoreboard();
    private static final Map<ChatFormatting, PlayerTeam> TEAM_MAP = new HashMap<>();

    public EntityGlowNMS() {
        for (ChatFormatting color : ChatFormatting.values()) {
            if (!color.isColor()) continue;

            PlayerTeam team = new PlayerTeam(NMS_SCOREBOARD, "omc_glow_" + color.getName());

            team.setColor(color);
            team.setSeeFriendlyInvisibles(false);
            team.setNameTagVisibility(PlayerTeam.Visibility.NEVER);

            TEAM_MAP.put(color, team);
        }
    }

    public static void setGlowingColor(Entity entity, ChatFormatting color) {
        entity.setGlowing(true);

        PlayerTeam team = TEAM_MAP.get(color);
        String entry = entity.getUniqueId().toString();

        ClientboundSetPlayerTeamPacket addEntityPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(
                        team,
                        entry,
                        ClientboundSetPlayerTeamPacket.Action.ADD
        );

        sendPacket(addEntityPacket);
    }

    public static void removeGlowing(Entity entity, ChatFormatting color) {
        entity.setGlowing(false);

        PlayerTeam team = TEAM_MAP.get(color);
        String entry = entity.getUniqueId().toString();

        ClientboundSetPlayerTeamPacket removeEntityPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(
                        team,
                        entry,
                        ClientboundSetPlayerTeamPacket.Action.REMOVE
                );

        sendPacket(removeEntityPacket);
    }

    public static void removeGlowing(Entity entity) {
        entity.setGlowing(false);

        for (ChatFormatting color : ChatFormatting.values()) {
            if (!color.isColor()) continue;

            removeGlowing(entity, color);
        }
    }

    private static void sendPacket(ClientboundSetPlayerTeamPacket packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (ChatFormatting color : ChatFormatting.values()) {
            if (!color.isColor()) continue;

            PlayerTeam team = TEAM_MAP.get(color);
            if (team == null) continue;

            sendPacket(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        }
    }
}

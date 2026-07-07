package fr.openmc.core.utils.nms.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityGlowNMS {
    private static final Scoreboard NMS_SCOREBOARD = new Scoreboard();

    public static void setGlowingColor(Entity entity, ChatFormatting color) {
        entity.setGlowing(true);

        String teamName = "omc_glow_" + color.getName();
        PlayerTeam team = new PlayerTeam(NMS_SCOREBOARD, teamName);

        team.setColor(color);
        team.setSeeFriendlyInvisibles(false);
        team.setNameTagVisibility(PlayerTeam.Visibility.NEVER);

        String entry = entity.getUniqueId().toString();

        ClientboundSetPlayerTeamPacket createPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(
                team, true);

        ClientboundSetPlayerTeamPacket addEntityPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(
                        team,
                        entry,
                        ClientboundSetPlayerTeamPacket.Action.ADD
                );

        sendPacket(createPacket);
        sendPacket(addEntityPacket);
    }

    public static void removeGlowing(Entity entity, ChatFormatting color) {
        entity.setGlowing(false);

        String teamName = "omc_glow_" + color.getName();
        PlayerTeam team = new PlayerTeam(NMS_SCOREBOARD, teamName);

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
}

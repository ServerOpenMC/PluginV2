package fr.openmc.core.features.leaderboards.entities;

import com.mojang.math.Transformation;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

public class TextDisplay {

    // Code pris de l'un de mes plugins et inspiré de HologramLib (Misieur)

    private final Set<UUID> viewerList = new HashSet<>();
    private final Display.TextDisplay textDisplay;
    private Location location;

    public TextDisplay(Component text, Location location, Vector3f scale) {
        this.location = location;

        textDisplay = new Display.TextDisplay(EntityType.TEXT_DISPLAY, ((CraftWorld) location.getWorld()).getHandle());
        textDisplay.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);
        textDisplay.getEntityData().set(new EntityDataAccessor<>(24, EntityDataSerializers.INT),Integer.MAX_VALUE);
        textDisplay.setInvisible(true);
        textDisplay.setBrightnessOverride(Brightness.FULL_BRIGHT);
        textDisplay.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), scale, new Quaternionf()));
        textDisplay.setText(PaperAdventure.asVanilla(text));
        update();
        updateViewersList();
    }

    public Set<Player> getPlayersWithinDistance(double radius) {
        return location.getWorld().getPlayers().stream()
                .filter(player -> player.getLocation().distanceSquared(location) <= radius * radius)
                .collect(Collectors.toSet());
    }

    private void addViewer(Player viewer) {
        if (viewerList.contains(viewer.getUniqueId())) return;
        viewerList.add(viewer.getUniqueId());
        ServerPlayer nmsViewer = ((CraftPlayer) viewer).getHandle();

        // Add Entity Packet
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                textDisplay.getId(),
                textDisplay.getUUID(),
                textDisplay.getX(),
                textDisplay.getY(),
                textDisplay.getZ(),
                0,
                0,
                EntityType.TEXT_DISPLAY,
                0,
                Vec3.ZERO,
                0
        );
        nmsViewer.connection.send(addEntityPacket);

        // Entity Data Packet
        List<SynchedEntityData.DataValue<?>> dataValues = textDisplay.getEntityData().getNonDefaultValues();
        if (dataValues == null || dataValues.isEmpty()) return;
        ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(textDisplay.getId(), textDisplay.getEntityData().getNonDefaultValues());
        nmsViewer.connection.send(entityDataPacket);
    }

    private void addViewers(Collection<Player> viewers) {
        viewers.forEach(this::addViewer);
    }

    private void removeViewer(UUID uuid) {
        viewerList.remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(textDisplay.getId());
        nmsPlayer.connection.send(removeEntitiesPacket);
    }

    private void removeViewers(Collection<UUID> viewers) {
        viewers.forEach(this::removeViewer);
    }

    public void updateViewersList() {
        Set<Player> viewersToKeep = getPlayersWithinDistance(100);

        Set<UUID> viewersToRemove = new HashSet<>(viewerList);
        viewersToKeep.forEach(player -> viewersToRemove.remove(player.getUniqueId()));

        removeViewers(viewersToRemove);
        addViewers(viewersToKeep);
    }

    public void updateText(Component text) {
        textDisplay.setText(PaperAdventure.asVanilla(text));
        update();
    }

    public void update() {
        if (viewerList.isEmpty()) return;
        List<SynchedEntityData.DataValue<?>> dataValues = textDisplay.getEntityData().getNonDefaultValues();
        if (dataValues == null || dataValues.isEmpty()) return;
        ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(textDisplay.getId(), textDisplay.getEntityData().getNonDefaultValues());
        viewerList.forEach(uuid -> {
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) {
                ServerPlayer nmsViewer = ((CraftPlayer) viewer).getHandle();
                nmsViewer.connection.send(entityDataPacket);
            }
        });
    }

    public void remove() {
        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(textDisplay.getId());
        viewerList.forEach(uuid -> {
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) {
                ServerPlayer player = ((CraftPlayer) viewer).getHandle();
                player.connection.send(removeEntitiesPacket);
            }
        });
        viewerList.clear();
        textDisplay.remove(Entity.RemovalReason.CHANGED_DIMENSION);
    }

    public void setScale(Vector3f scale) {
        textDisplay.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), scale, new Quaternionf()));
        update();
    }

    public void setLocation(Location location) {
        this.location = location;
        textDisplay.setPos(location.getX(), location.getY(), location.getZ());
        textDisplay.setRot(location.getYaw(), location.getPitch());
        ClientboundTeleportEntityPacket teleportEntityPacket = ClientboundTeleportEntityPacket.teleport(
                textDisplay.getId(),
                PositionMoveRotation.of(textDisplay),
                new HashSet<>(),
                false
        );
        viewerList.forEach(uuid -> {
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) {
                ServerPlayer nmsViewer = ((CraftPlayer) viewer).getHandle();
                nmsViewer.connection.send(teleportEntityPacket);
            }
        });
    }

}

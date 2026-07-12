package fr.openmc.core.utils.world.entities;

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
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TextDisplay {

    private final Set<UUID> viewerList = new HashSet<>();
    private final Display.TextDisplay textDisplay;
    private Location location;

    public TextDisplay(Component text, Location location, Vector3f scale) {
        this.location = location;
        textDisplay = new Display.TextDisplay(EntityTypes.TEXT_DISPLAY, ((CraftWorld) location.getWorld()).getHandle());
        textDisplay.setPos(location.getX(), location.getY(), location.getZ());
        textDisplay.setRot(location.getYaw(), location.getPitch());
        textDisplay.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        textDisplay.getEntityData().set(new EntityDataAccessor<>(24, EntityDataSerializers.INT), Integer.MAX_VALUE);
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
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                textDisplay.getId(),
                textDisplay.getUUID(),
                textDisplay.getX(),
                textDisplay.getY(),
                textDisplay.getZ(),
                textDisplay.getXRot(),
                textDisplay.getYRot(),
                EntityTypes.TEXT_DISPLAY,
                0,
                Vec3.ZERO,
                0
        );
        nmsViewer.connection.send(addEntityPacket);

        List<SynchedEntityData.DataValue<?>> dataValues = textDisplay.getEntityData().getNonDefaultValues();
        if (dataValues == null || dataValues.isEmpty()) return;
        nmsViewer.connection.send(new ClientboundSetEntityDataPacket(textDisplay.getId(), dataValues));
    }

    private void addViewers(Collection<Player> viewers) {
        viewers.forEach(this::addViewer);
    }

    private void removeViewer(UUID uuid) {
        viewerList.remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.connection.send(new ClientboundRemoveEntitiesPacket(textDisplay.getId()));
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
        ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(textDisplay.getId(), dataValues);
        viewerList.forEach(uuid -> {
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) {
                ((CraftPlayer) viewer).getHandle().connection.send(entityDataPacket);
            }
        });
    }

    public void remove() {
        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(textDisplay.getId());
        viewerList.forEach(uuid -> {
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) {
                ((CraftPlayer) viewer).getHandle().connection.send(removeEntitiesPacket);
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
                ((CraftPlayer) viewer).getHandle().connection.send(teleportEntityPacket);
            }
        });
    }
}

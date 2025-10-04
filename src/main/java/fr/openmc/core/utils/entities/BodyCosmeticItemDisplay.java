package fr.openmc.core.utils.entities;

import fr.openmc.core.features.cosmetics.CosmeticManager;
import fr.openmc.core.features.cosmetics.bodycostmetics.BodyCosmetic;
import fr.openmc.core.utils.MathUtils;
import fr.openmc.core.utils.PacketUtils;
import lombok.ToString;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ToString
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class BodyCosmeticItemDisplay {

    private final ServerPlayer player;
    private final BodyCosmetic bodyCosmetic;
    private final Set<UUID> viewerList = new HashSet<>();
    private final Display.ItemDisplay itemDisplay;
    private final int entityId;


    public BodyCosmeticItemDisplay(ServerPlayer player, BodyCosmetic bodyCosmetic) {
        this.player = player;
        this.bodyCosmetic = bodyCosmetic;

        itemDisplay = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, player.level());

        itemDisplay.setPos(player.getX(), player.getY(), player.getZ());
        itemDisplay.setItemStack(CraftItemStack.asNMSCopy(bodyCosmetic.getItem()));
        itemDisplay.setBillboardConstraints(Display.BillboardConstraints.FIXED);
        itemDisplay.setItemTransform(ItemDisplayContext.HEAD);
        itemDisplay.setInvisible(true);
        entityId = itemDisplay.getId();
        update();
        updateViewersList();
    }

    public Set<Player> getPlayersWithinDistance(Vec3 center, double radius) {
        Location location = new Location(player.level().getWorld(), center.x, center.y, center.z);
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
                entityId,
                itemDisplay.getUUID(),
                player.getX(),
                player.getY(),
                player.getZ(),
                0,
                0,
                EntityType.ITEM_DISPLAY,
                0,
                Vec3.ZERO,
                0
        );
        nmsViewer.connection.send(addEntityPacket);

        // Passenger Packet
        nmsViewer.connection.send(PacketUtils.createSetPassengersPacket(player, entityId));

        // Entity Data Packet
        ClientboundSetEntityDataPacket entityDataPacket = getEntityDataPacket();
        nmsViewer.connection.send(entityDataPacket);
    }

    private ClientboundSetEntityDataPacket getEntityDataPacket() {

        return new ClientboundSetEntityDataPacket(
                entityId, itemDisplay.getEntityData().packAll()
        );

    }

    private void addViewers(Collection<Player> viewers) {
        viewers.forEach(this::addViewer);
    }

    private void removeViewer(UUID viewerUUID) {
        viewerList.remove(viewerUUID);
        OfflinePlayer viewer = Bukkit.getPlayer(viewerUUID);
        if (viewer == null) return;
        ServerPlayer player = ((CraftPlayer) viewer).getHandle();
        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(entityId);
        player.connection.send(removeEntitiesPacket);
    }

    private void removeViewers(Collection<UUID> viewers) {
        viewers.forEach(this::removeViewer);
    }

    public void updateViewersList() {
        Set<Player> viewersToKeep = getPlayersWithinDistance(player.getEyePosition(), 100);

        Set<UUID> viewersToRemove = new HashSet<>(viewerList);
        viewersToKeep.forEach(player -> viewersToRemove.remove(player.getUniqueId()));

        removeViewers(viewersToRemove);
        addViewers(viewersToKeep);
    }

    public void updateTransformation() {
        float bodyYaw = CosmeticManager.getBodyCosmeticsManager().getPlayerBodyYaw(player.getUUID());
        itemDisplay.setTransformation(
                MathUtils.getTransformation(
                        bodyYaw,
                        player.isShiftKeyDown() && !player.getAbilities().flying,
                        player.isSwimming(),
                        player.isFallFlying(),
                        player.getXRot()
                )
        );
        itemDisplay.setTransformationInterpolationDuration(1);
        update();
    }

    public void update() {
        viewerList.forEach(uuid -> {
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) {
                ServerPlayer nmsViewer = ((CraftPlayer) viewer).getHandle();
                ClientboundSetEntityDataPacket entityDataPacket = getEntityDataPacket();
                nmsViewer.connection.send(entityDataPacket);
            }
        });
    }

    private record Position(float x, float z) {
    }

    public void remove() {
        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(entityId);
        viewerList.forEach(uuid -> {
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) {
                ServerPlayer player = ((CraftPlayer) viewer).getHandle();
                player.connection.send(removeEntitiesPacket);
            }
        });
        viewerList.clear();
        itemDisplay.remove(Entity.RemovalReason.CHANGED_DIMENSION);
    }

}

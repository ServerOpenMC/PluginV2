package fr.openmc.core.features.animations.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import dev.lone.itemsadder.api.Events.PlayerEmoteEndEvent;
import dev.lone.itemsadder.api.Events.PlayerEmotePlayEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.animations.Animation;
import fr.openmc.core.features.animations.PlayerAnimationInfo;
import fr.openmc.core.utils.EnumUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class EmoteListener implements Listener {
    public static final HashMap<Player, PlayerAnimationInfo> playingAnimations = new HashMap<>();

    @EventHandler
    public void onAnimationStart(PlayerEmotePlayEvent e) {
        Animation animation = EnumUtils.match(e.getEmoteName(), Animation.class);
        if (animation == null) return;

        Player player = e.getPlayer();
        Location base = player.getLocation();

        if (animation.getSoundName() != null) {
            player.playSound(player, animation.getSoundName(), 1.0f, 1.0f);
        }


        playingAnimations.put(player, new PlayerAnimationInfo());
        EmoteListener.setupHead(player);

        ArmorStand stand = player.getWorld().spawn(base, ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setGravity(false);
            as.setMarker(true);
        });

        playingAnimations.get(player).setArmorStand(stand);

        BukkitTask task = new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick > animation.getTotalTicks()) {
                    animationEnd(player);
                    return;
                }

                if (animation.getCameraPositions().containsKey(tick) && animation.getCameraViews().containsKey(tick)) {
                    Vector pos = animation.getCameraPositions().get(tick);
                    Vector view = animation.getCameraViews().get(tick);

                    Location camLoc = base.clone().add(pos);

                    Vector lookTarget = camLoc.toVector().add(new Vector(view.getX(), 0, view.getZ()));

                    Vector diff = lookTarget.clone().subtract(camLoc.toVector());
                    if (diff.lengthSquared() < 1e-6) { // obligé pour éviter une java.lang.IllegalArgumentException: pitch not finite
                        diff = new Vector(0, 0, 1);
                    }
                    Vector direction = diff.normalize();
                    camLoc.setDirection(direction);

                    stand.teleport(camLoc);
                    sendCamera(player, stand);
                }

                tick++;
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 1L);

        playingAnimations.get(player).setTask(task);
    }

    @EventHandler
    public void onAnimationEndOrStop(PlayerEmoteEndEvent e) {
        Animation animation = EnumUtils.match(e.getEmoteName(), Animation.class);
        if (animation == null) return;

        animationEnd(e.getPlayer());
    }

    private void animationEnd(Player player) {
        PlayerAnimationInfo info = playingAnimations.remove(player);
        if (info == null) return;

        sendCamera(player, player);
        if (info.getArmorStand() != null)
            info.getArmorStand().remove();

        if (info.getTask() != null)
            info.getTask().cancel();

        restoreHead(player);
    }

    /**
     * Sends a packet to the player to set the camera to the specified entity.
     *
     * @param player The player to send the packet to.
     * @param entity The entity to set the camera to.
     */
    private void sendCamera(Player player, Entity entity) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CAMERA);
        packet.getIntegers().write(0, entity.getEntityId());
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    public static void setupHead(Player player) {
        PlayerAnimationInfo info = playingAnimations.get(player);
        if (info == null) return;
        info.setOldRotations(new Float[]{player.getLocation().getYaw(), player.getLocation().getPitch()});

        Location loc = player.getLocation().clone();
        loc.setYaw(180f);
        loc.setPitch(0f);

        player.teleport(loc);
    }

    private void restoreHead(Player player) {
        PlayerAnimationInfo info = playingAnimations.get(player);
        if (info == null) return;

        Float[] rot = info.getOldRotations();
        Location loc = player.getLocation().clone();
        loc.setYaw(rot[0]);
        loc.setPitch(rot[1]);
        player.teleport(loc);
    }
}

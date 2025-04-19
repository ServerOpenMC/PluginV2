package fr.openmc.core.utils.animations;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import dev.lone.itemsadder.api.CustomPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Animations {
	
	/**
	 * Sets the camera of the player to the specified location and plays the animation.
	 *
	 * @param player        The player to set the camera for.
	 * @param animationName The name of the animation to play.
	 */
	public static void setAndPlay(@NotNull Player player, String animationName) {
		ItemStack item = player.getInventory().getItemInMainHand();
		item.setAmount(player.getInventory().getItemInMainHand().getAmount());
		player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		
		Location location = player.getLocation();
		
		Location targetLocation = location.clone().add(player.getLocation().getDirection().normalize().multiply(3));
		targetLocation.setY(player.getLocation().getY());
		targetLocation.setRotation(targetLocation.getYaw() - 180, 0);

		Entity entity = player.getWorld().spawnEntity(targetLocation, EntityType.ARMOR_STAND);
		entity.setInvisible(true);
		entity.setGravity(false);
		
		sendPacket(player, entity, protocolManager);
		
		System.out.println(entity.getLocation());
		
		CustomPlayer.playEmote(player, animationName);
		try {
			TimeUnit.MILLISECONDS.sleep(1500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		sendPacket(player, player, protocolManager);
		entity.remove();
		player.setWalkSpeed(0.2f);
		player.getInventory().setItemInMainHand(item);
	}
	
	/**
	 * Sends a packet to the player to set the camera to the specified entity.
	 *
	 * @param player  The player to send the packet to.
	 * @param entity  The entity to set the camera to.
	 * @param protocolManager The ProtocolManager instance.
	 */
	private static void sendPacket(Player player, @NotNull Entity entity, ProtocolManager protocolManager) {
		PacketContainer setCamera = new PacketContainer(PacketType.Play.Server.CAMERA);
		setCamera.getIntegers().write(0, entity.getEntityId());
		protocolManager.sendServerPacket(player, setCamera);
	}
}

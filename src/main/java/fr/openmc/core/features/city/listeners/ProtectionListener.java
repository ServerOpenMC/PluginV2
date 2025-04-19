package fr.openmc.core.features.city.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class ProtectionListener implements Listener {

    private boolean isMemberOf(@Nullable City city, Player player) {
        if (city == null) {
            return true;
        }

        return city.getMembers().contains(player.getUniqueId());
    }

    @Nullable
    private City getCityByChunk(Chunk chunk) {
        for (City city: CityManager.getCities()) {
            if (city.getChunks().contains(BlockVector2.at(chunk.getX(), chunk.getZ()))) {
                return city;
            }
        }
        return null;
    }

    private void verify(Player player, Cancellable event, Location loc) {
        City city = getCityByChunk(loc.getChunk()); // on regarde le claim ou l'action a été fait
        City cityz = CityManager.getPlayerCity(player.getUniqueId()); // on regarde la city du membre

        if (isMemberOf(city, player)) return;
        if (cityz!=null){
            String city_type = CityManager.getCityType(city.getUUID());
            String cityz_type = CityManager.getCityType(cityz.getUUID());
            if (city_type!=null && cityz_type!=null){
                if (city_type.equals("war") && cityz_type.equals("war")){
                    return;
                }
            }
        }
        event.setCancelled(true);

        MessagesManager.sendMessage(player, Component.text("Vous n'avez pas l'autorisation de faire ceci !"), Prefix.CITY, MessageType.ERROR, true);
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) { verify(event.getPlayer(), event, event.getBlock().getLocation()); }

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // évite les doublons
        if (event.getInteractionPoint() == null && event.getClickedBlock() == null) return;

        Location loc = event.getInteractionPoint() != null ?
                event.getInteractionPoint() :
                event.getClickedBlock().getLocation();

        verify(event.getPlayer(), event, loc);
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;

        Location loc = event.getEntity().getLocation();
        verify(damager, event, loc);
    }

    @EventHandler
    void onInteractAtEntity(PlayerInteractAtEntityEvent event) { verify(event.getPlayer(), event, event.getRightClicked().getLocation()); }

    @EventHandler
    void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event instanceof PlayerInteractAtEntityEvent) return;

        verify(event.getPlayer(), event, event.getRightClicked().getLocation());
    }

    @EventHandler
    void onFish(PlayerFishEvent event) { verify(event.getPlayer(), event, event.getHook().getLocation()); }

    @EventHandler
    void onShear(PlayerShearEntityEvent event) { verify(event.getPlayer(), event, event.getEntity().getLocation()); }

    @EventHandler
    void onLeash(PlayerLeashEntityEvent event) { verify(event.getPlayer(), event, event.getEntity().getLocation()); }

    @EventHandler
    void onUnleash(PlayerUnleashEntityEvent event) { verify(event.getPlayer(), event, event.getEntity().getLocation()); }

    @EventHandler
    void onLaunchProjectile(PlayerLaunchProjectileEvent event) {
        verify(event.getPlayer(), event, event.getPlayer().getLocation());
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        verify(player, event, event.getEntity().getLocation());
    }

    @EventHandler
    public void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile.getShooter() instanceof Player player)) return;

        verify(player, event, event.getEntity().getLocation());
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame || event.getEntity() instanceof Painting) {
            if (event.getRemover() instanceof Player) {
                Player player = (Player) event.getRemover();
                verify(player, event, event.getEntity().getLocation());
            }
        }
    }

    @EventHandler
    void onPlaceBlock(BlockPlaceEvent event) { verify(event.getPlayer(), event, event.getBlockPlaced().getLocation()); }
}
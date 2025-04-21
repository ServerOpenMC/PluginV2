package fr.openmc.core.features.city.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
        if (player.getWorld() != Bukkit.getWorld("world")) return;
        
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

        MessagesManager.sendMessage(player, Component.text("Vous n'avez pas l'autorisation de faire ceci !"), Prefix.CITY, MessageType.ERROR, 0.6F, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFoodConsume(PlayerItemConsumeEvent event) {
        // on laisse les gens manger
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed tnt) {
            if (tnt.getSource() instanceof Player player) {
                City cityz = CityManager.getPlayerCity(player.getUniqueId());

                event.blockList().removeIf(block -> {
                    City blockCity = getCityByChunk(block.getChunk());

                    return !isMemberOf(blockCity, player) &&
                            !(
                                    blockCity != null &&
                                            cityz != null &&
                                            CityManager.getCityType(blockCity.getUUID()).equals("war") &&
                                            CityManager.getCityType(cityz.getUUID()).equals("war")
                            );
                });
            }
        }
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) { verify(event.getPlayer(), event, event.getBlock().getLocation()); }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();

        if (hand == EquipmentSlot.OFF_HAND) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                Location loc = event.getClickedBlock() != null
                        ? event.getClickedBlock().getLocation()
                        : player.getLocation();
                verify(player, event, loc);
            }
            return;
        }

        ItemStack inHand = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_AIR && inHand != null && inHand.getType().isEdible()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (inHand != null && inHand.getType().isEdible()) {
                Block clicked = event.getClickedBlock();
                Material type = clicked.getType();

                if (!type.isInteractable()) return;
            }

            Location loc = event.getInteractionPoint() != null
                    ? event.getInteractionPoint()
                    : event.getClickedBlock().getLocation();

            verify(player, event, loc);
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            verify((Player) event.getEntity(), event, block.getLocation());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() == Material.FARMLAND) {
                verify(event.getPlayer(), event, event.getClickedBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;

        if (MascotUtils.isMascot(event.getEntity())) return;
        Location loc = event.getEntity().getLocation();
        verify(damager, event, loc);
    }

    @EventHandler
    void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event instanceof PlayerInteractAtEntityEvent) return;
        if (event instanceof PlayerInteractEntityEvent) return;

        verify(event.getPlayer(), event, event.getRightClicked().getLocation());
    }

    @EventHandler
    void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event instanceof PlayerInteractAtEntityEvent) return;
        if (event instanceof PlayerInteractEntityEvent) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        if (MascotUtils.isMascot(event.getRightClicked())) return;

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
    public void onCreeperExplode(EntityExplodeEvent event) {
        Chunk chunk = event.getLocation().getChunk();
        City city = CityManager.getCityFromChunk(chunk.getX(), chunk.getZ());
        if (city == null) return;

        if (Objects.equals(CityManager.getCityType(city.getUUID()), "peace")) {
            if (event.getEntityType() == EntityType.CREEPER) {
                event.blockList().clear();
            }
        }
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

    @EventHandler
    public void onFireIgnite(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (player==null) return;

        verify(event.getPlayer(), event, loc);
    }
}
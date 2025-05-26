package fr.openmc.core.features.city.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProtectionListener implements Listener {

    public static HashMap<UUID, Boolean> playerCanBypass = new HashMap<>();

    private static final Map<UUID, Long> lastErrorMessageTime = new HashMap<>();
    private static final long ERROR_MESSAGE_COOLDOWN = 3000; // 3 secondes

    private void verify(Player player, Cancellable event, Location loc) {
        if (!player.getWorld().getName().equals("world")) return;

        Boolean canBypass = playerCanBypass.get(player.getUniqueId());
        if (canBypass != null && canBypass) return;

        City cityAtLoc = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        if (cityAtLoc == null) return;

        CityType cityType = cityAtLoc.getType();
        boolean isMember = cityAtLoc.isMember(player);

        if (cityType.equals(CityType.WAR)) {
            return;
        }

        if (!isMember) {
            event.setCancelled(true);

            long now = System.currentTimeMillis();
            long last = lastErrorMessageTime.getOrDefault(player.getUniqueId(), 0L);
            if (now - last >= ERROR_MESSAGE_COOLDOWN) {
                lastErrorMessageTime.put(player.getUniqueId(), now);
                MessagesManager.sendMessage(
                        player,
                        Component.text("Vous n'avez pas l'autorisation de faire ceci !"),
                        Prefix.CITY,
                        MessageType.ERROR,
                        0.6F,
                        true
                );
            }
        }
    }

    private void cancelWithMessage(Player player, Cancellable event) {
        event.setCancelled(true);

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long last = lastErrorMessageTime.getOrDefault(uuid, 0L);

        if (now - last >= ERROR_MESSAGE_COOLDOWN) {
            lastErrorMessageTime.put(uuid, now);
            MessagesManager.sendMessage(
                    player,
                    Component.text("Vous n'avez pas l'autorisation de faire ceci !"),
                    Prefix.CITY,
                    MessageType.ERROR,
                    0.6F,
                    true
            );
        }
    }

    private void verify(Entity entity, Cancellable event, Location loc) {
        if (!entity.getWorld().getName().equals("world")) return;

        City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ()); // on regarde le claim ou l'action a été fait
        if (city == null || !CityType.WAR.equals(city.getType()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFoodConsume(PlayerItemConsumeEvent event) {
        // on laisse les gens manger
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) { verify(event.getPlayer(), event, event.getBlock().getLocation()); }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        ItemStack inHand = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_AIR && inHand != null && inHand.getType().isEdible()) {
            return;
        }

        if (event.getClickedBlock() == null) return;

        Location loc = event.getClickedBlock().getLocation();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (inHand != null && inHand.getType().isEdible()) {
                Block clicked = event.getClickedBlock();
                Material type = clicked.getType();

                if (!type.isInteractable()) return;
            }

            verify(player, event, loc);
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            verify(event.getEntity(), event, block.getLocation());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() == Material.FARMLAND) {
                verify(event.getPlayer(), event, event.getClickedBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        Player attacker = null;
        if (damager instanceof Player p) {
            attacker = p;
        } else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player shooter) {
            attacker = shooter;
        }

        if (victim instanceof Player victimPlayer && attacker != null) {
            Location loc = victimPlayer.getLocation();
            City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());

            if (city != null
                    && city.isMember(victimPlayer)
                    && city.isMember(attacker)) {

                if (!city.getLaw().isPvp()) {
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }

        if (victim instanceof Player victimPlayer) {
            verify(victimPlayer, event, victimPlayer.getLocation());
            if (event.isCancelled()) return;
        }

        if (MascotUtils.isMascot(victim)) return;

        if (attacker != null) {
            verify(attacker, event, victim.getLocation());
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof ItemFrame || entity instanceof GlowItemFrame || entity instanceof Hanging) {
            verify(player, event, entity.getLocation());
        }
    }

    @EventHandler
    void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        Entity rightClicked = event.getRightClicked();

        if (rightClicked instanceof Player) return;
        if (MascotUtils.isMascot(rightClicked)) return;

        verify(event.getPlayer(), event, rightClicked.getLocation());
    }

//    @EventHandler
//    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
//        verify(event.getPlayer(), event, event.getRightClicked().getLocation());
//    }

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

    private static final List<EntityType> NATURAL_EXPLOSIVE_ENTITIES = List.of(
            EntityType.CREEPER,
            EntityType.FIREBALL,
            EntityType.SMALL_FIREBALL,
            EntityType.WITHER_SKULL,
            EntityType.WITHER,
            EntityType.END_CRYSTAL,
            EntityType.TNT_MINECART,
            EntityType.DRAGON_FIREBALL
    );

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed tnt && tnt.getSource() instanceof Player player) {
            City cityz = CityManager.getPlayerCity(player.getUniqueId());

            event.blockList().removeIf(block -> {
                City blockCity = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());
                if (blockCity == null) return false;

                if (blockCity.isMember(player)) return false;
                if (cityz != null) {
                    CityType type1 = blockCity.getType();
                    CityType type2 = cityz.getType();

                    return !(type1 != null && type2 != null && type1.equals(CityType.WAR) && type2.equals(CityType.WAR));
                }
                return true;
            });
            return;
        }

        if (entity instanceof TNTPrimed) {
            event.blockList().removeIf(block -> {
                City city = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());
                return city != null && CityType.PEACE.equals(city.getType());
            });
            return;
        }

        if (NATURAL_EXPLOSIVE_ENTITIES.contains(entity.getType())) {
            event.blockList().removeIf(block -> {
                City city = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());
                return city != null && CityType.PEACE.equals(city.getType());
            });
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            City blockCity = CityManager.getCityFromChunk(block.getChunk().getX(), block.getChunk().getZ());

            return blockCity != null && blockCity.getType().equals(CityType.PEACE);
        });
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.WITHER || entity.getType() == EntityType.WITHER_SKULL) {
            City city = CityManager.getCityFromChunk(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
            if (city != null && city.getType().equals(CityType.PEACE)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getRemover() instanceof Player player) {
            verify(player, event, event.getEntity().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlaceBlock(BlockPlaceEvent event) {
        verify(event.getPlayer(), event, event.getBlock().getLocation());
    }

    @EventHandler
    public void onFireIgnite(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (player==null) return;

        verify(event.getPlayer(), event, loc);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        ProjectileSource shooter = potion.getShooter();

        if (!(shooter instanceof Witch witch))
            return;

        Location witchLocation = witch.getLocation();
        City city = CityManager.getCityFromChunk(witchLocation.getChunk().getX(), witchLocation.getChunk().getZ());
        if (city == null)
            return;

        boolean isCityInWar = city.getType().equals(CityType.WAR);

        for (LivingEntity affectedEntity : event.getAffectedEntities()) {
            if (!(affectedEntity instanceof Player player))
                continue;

            boolean isNotMember = !city.isMember(player);
            if (!isNotMember || isCityInWar)
                continue;

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInventoryOpen(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Merchant || entity instanceof InventoryHolder) {
            verify(player, event, entity.getLocation());
        }
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        Entity mount = event.getMount();

        if (!(mount instanceof Tameable tameable)) return;


        if (!tameable.isTamed()) return;

        if (!tameable.getOwnerUniqueId().equals(player.getUniqueId())) verify(player, event, mount.getLocation());
    }
}
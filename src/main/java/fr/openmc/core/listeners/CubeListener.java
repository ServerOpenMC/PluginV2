package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

// Les Restes du Cube. Aucun mouvement possible, juste pour le lore, les souvenirs, l'easter egg, bref :)
// - iambibi_
public class CubeListener implements Listener {
    private OMCPlugin plugin;
    private static final int CUBE_SIZE = 5;
    private final Material CUBE_MATERIAL = Material.LAPIS_BLOCK;
    private BossBar bossBar;
    static double currentX = -171.0;
    static double currentZ = -117.0;
    static double currentY = Bukkit.getWorld("world").getHighestBlockYAt((int) currentX, (int) currentZ);
    public static Location currentLocation = new Location(Bukkit.getWorld("world"), currentX, currentY, currentZ);


    public CubeListener(OMCPlugin plugin) {
        this.plugin = plugin;
        bossBar = Bukkit.createBossBar("Le Cube", BarColor.BLUE, BarStyle.SOLID, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
        bossBar.setVisible(true);
        startBossBarUpdater();
        createCube(currentLocation);
    }

    private void startBossBarUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(currentLocation.getWorld())) {
                        double distance = player.getLocation().distance(currentLocation);

                        if (distance <= 50) {
                            if (!bossBar.getPlayers().contains(player)) {
                                bossBar.addPlayer(player);
                            }
                        } else {
                            bossBar.removePlayer(player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }


    private void createCube(Location location) {
        World world = location.getWorld();
        int baseX = location.getBlockX();
        int baseY = location.getBlockY();
        int baseZ = location.getBlockZ();

        for (int x = 0; x < CUBE_SIZE; x++) {
            for (int y = 0; y < CUBE_SIZE; y++) {
                for (int z = 0; z < CUBE_SIZE; z++) {
                    Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                    if (block.getType() != CUBE_MATERIAL) {
                        block.setType(CUBE_MATERIAL);
                    }
                }
            }
        }
    }

    public static void clearCube(Location location) {
        for (int x = 0; x < CUBE_SIZE; x++) {
            for (int y = 0; y < CUBE_SIZE; y++) {
                for (int z = 0; z < CUBE_SIZE; z++) {
                    location.clone().add(x, y, z).getBlock().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Location clickedBlock = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null;
        if (clickedBlock != null && isCubeBlock(clickedBlock)) {
            repulsePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && isCubeBlock(event.getEntity().getLocation())) {
            repulsePlayer(player);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isCubeBlock(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location belowPlayer = player.getLocation().clone().subtract(0, 1, 0);

        if (isCubeBlock(belowPlayer)) {
            Vector velocity = player.getVelocity();
            velocity.setY(1.0);
            player.setVelocity(velocity);

            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 2.0f);
        }
    }

    private boolean isCubeBlock(Location blockLocation) {
        int x = blockLocation.getBlockX();
        int y = blockLocation.getBlockY();
        int z = blockLocation.getBlockZ();

        int cubeX = currentLocation.getBlockX();
        int cubeY = currentLocation.getBlockY();
        int cubeZ = currentLocation.getBlockZ();

        return x >= cubeX && x < cubeX + CUBE_SIZE &&
                y >= cubeY && y < cubeY + CUBE_SIZE &&
                z >= cubeZ && z < cubeZ + CUBE_SIZE &&
                blockLocation.getBlock().getType() == CUBE_MATERIAL;
    }

    private void repulsePlayer(Player player) {
        Vector direction = player.getLocation().toVector().subtract(currentLocation.toVector()).normalize();
        direction.multiply(3);
        direction.setY(1);
        player.setVelocity(direction);

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 2.0f);
    }
}


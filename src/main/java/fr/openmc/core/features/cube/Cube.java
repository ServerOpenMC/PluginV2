package fr.openmc.core.features.cube;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.cube.listeners.RepulseEffectListener;
import fr.openmc.core.features.cube.multiblocks.MultiBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Powerable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

// Les Restes du Cube. Aucun mouvement possible, juste pour le lore, les souvenirs, l'easter egg, bref :)
// - iambibi_
public class Cube extends MultiBlock {
    public BukkitTask corruptedBubbleTask;
    public ReproductionTask reproductionTask;
    public BossBar cubeBossBar;
    public boolean showBossBar;

    public Cube(Location origin, int size, Material material, boolean showBossBar) {
        super(origin, size, material);

        // ## BOSS BAR ##
        if (showBossBar) {
            cubeBossBar = Bukkit.createBossBar("Le Cube", BarColor.BLUE, BarStyle.SEGMENTED_6, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
            cubeBossBar.setVisible(true);

            startBossBarUpdater();
        }

        startEventsCycle();
    }

    @Override
    public void build() {
        World world = this.origin.getWorld();
        int baseX = this.origin.getBlockX();
        int baseY = this.origin.getBlockY();
        int baseZ = this.origin.getBlockZ();

        for (int x = 0; x < this.radius; x++) {
            for (int y = 0; y < this.radius; y++) {
                for (int z = 0; z < this.radius; z++) {
                    Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                    block.setType(material);
                }
            }
        }
    }

    @Override
    public void clear() {
        World world = this.origin.getWorld();
        int baseX = this.origin.getBlockX();
        int baseY = this.origin.getBlockY();
        int baseZ = this.origin.getBlockZ();

        for (int x = 0; x < this.radius; x++) {
            for (int y = 0; y < this.radius; y++) {
                for (int z = 0; z < this.radius; z++) {
                    Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                    if (block.getType() == this.material) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public boolean isPartOf(Location loc) {
        if (loc == null) return false;
        if (loc.getBlock().getType().equals(Material.AIR)) return false;
        if (!loc.getBlock().getType().equals(this.material)) return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int baseX = this.origin.getBlockX();
        int baseY = this.origin.getBlockY();
        int baseZ = this.origin.getBlockZ();

        return x >= baseX && x < baseX + this.radius &&
                y >= baseY && y < baseY + this.radius &&
                z >= baseZ && z < baseZ + this.radius &&
                loc.getBlock().getType() == this.material;
    }

    public Location getCenter() {
        return this.origin.clone().add(this.radius / 2.0, this.radius / 2.0, this.radius / 2.0);
    }

    public void repulsePlayer(Player player, boolean isOnCube) {
        Vector velocity = isOnCube ? player.getVelocity() :
                player.getLocation().toVector().subtract(this.getCenter().toVector()).normalize();

        velocity.setY(1);


        if (!isOnCube) {
            velocity.multiply(3);
        }

        player.setVelocity(velocity);

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (!RepulseEffectListener.noFallPlayers.contains(player.getUniqueId())) {
                RepulseEffectListener.noFallPlayers.add(player.getUniqueId());
                RepulseEffectListener.startNoFallParticles(player);
            }
        }, 2L);

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 2.0f);
    }

    public void startMagneticShock() {
        World world = this.origin.getWorld();

        world.strikeLightningEffect(this.getCenter());

        int shockRadius = this.radius * 5;

        Location center = this.getCenter();
        int rays = 60;
        for (int i = 0; i < rays; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(2 * Math.random() - 1);
            Vector dir = new Vector(
                    Math.sin(phi) * Math.cos(theta),
                    Math.cos(phi),
                    Math.sin(phi) * Math.sin(theta)
            ).normalize();

            for (int j = 1; j <= shockRadius; j++) {
                Location point = center.clone().add(dir.clone().multiply(j));
                world.spawnParticle(
                        Particle.ELECTRIC_SPARK,
                        point,
                        2,
                        0.05, 0.05, 0.05,
                        0.01
                );
            }
        }

        for (int x = -shockRadius; x <= shockRadius; x++) {
            for (int y = -shockRadius; y <= shockRadius; y++) {
                for (int z = -shockRadius; z <= shockRadius; z++) {
                    Location loc = this.getCenter().clone().add(x, y, z);
                    Block block = loc.getBlock();
                    BlockData data = block.getBlockData();

                    if (data instanceof Powerable powerable) {
                        powerable.setPowered(!powerable.isPowered());
                        block.setBlockData(powerable, true);

                        world.spawnParticle(Particle.ELECTRIC_SPARK, loc.add(0.5, 0.5, 0.5), 8,
                                0.2, 0.2, 0.2
                        );
                    }

                    if (data instanceof Lightable lightable && data instanceof Powerable) {
                        lightable.setLit(!lightable.isLit());
                        block.setBlockData(lightable, true);

                        world.spawnParticle(Particle.ENCHANT, loc.add(0.5, 0.5, 0.5), 8,
                                0.2, 0.2, 0.2
                        );
                    }
                }
            }
        }

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distance(this.getCenter()) <= shockRadius) {
                world.spawnParticle(Particle.FLASH, player.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0.01);
                world.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
            } else {
                world.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.2f, 2f);
            }
        }
    }

    public final int RADIUS_BUBBLE = this.radius * 3;

    public void startCorruptedBubble() {
        Location center = this.getCenter();

        int totalTicks = 20 * 3600;

        startBubbleParticles();

        int intervalCorruption = 20 * 20;
        corruptedBubbleTask = new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                if (elapsed >= totalTicks) {
                    cancel();
                    corruptedBubbleTask = null;
                    return;
                }

                for (int i = 0; i < 30; i++) {
                    double theta = Math.random() * 2 * Math.PI;
                    double phi = Math.random() * Math.PI;
                    double r = Math.random() * RADIUS_BUBBLE;

                    double x = r * Math.sin(phi) * Math.cos(theta);
                    double y = r * Math.cos(phi);
                    double z = r * Math.sin(phi) * Math.sin(theta);

                    if (isPartOf(new Location(origin.getWorld(), x, y, z))) continue;

                    Location loc = center.clone().add(x, y, z);
                    Block block = loc.getBlock();
                    Material type = block.getType();

                    switch (type) {
                        case DIRT, GRASS_BLOCK, SAND, GRAVEL -> block.setType(Material.WARPED_NYLIUM);
                        case OAK_LOG, BIRCH_LOG, SPRUCE_LOG, JUNGLE_LOG, DARK_OAK_LOG,
                             ACACIA_LOG, MANGROVE_LOG -> block.setType(Material.WARPED_STEM);
                        case AIR, LAPIS_BLOCK -> {
                        }
                        default -> block.setType(Material.SCULK);
                    }
                }

                elapsed += intervalCorruption;
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, intervalCorruption);
    }

    public void startBubbleParticles() {
        World world = this.origin.getWorld();
        Location center = this.getCenter();
        double radius = RADIUS_BUBBLE;

        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            for (int i = 0; i < 50; i++) {
                double theta = Math.random() * 2 * Math.PI;
                double phi = Math.random() * Math.PI;
                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.cos(phi);
                double z = radius * Math.sin(phi) * Math.sin(theta);

                Location particleLoc = center.clone().add(x, y, z);
                world.spawnParticle(Particle.OMINOUS_SPAWNING, particleLoc, 1, 0.1, 0.1, 0.1, 0);
            }

            for (double theta = 0; theta < Math.PI; theta += Math.PI / 16) {
                for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 16) {
                    double x = radius * Math.sin(theta) * Math.cos(phi);
                    double y = radius * Math.cos(theta);
                    double z = radius * Math.sin(theta) * Math.sin(phi);

                    Location particleLoc = center.clone().add(x, y, z);

                    Vector dir = center.clone().subtract(particleLoc).toVector().normalize();
                    world.spawnParticle(Particle.SNEEZE, particleLoc, 1, dir.getX(), dir.getY(), dir.getZ(), 0.1);
                }
            }
        }, 0L, 20L);
    }

    public void startReproduction() {
        World world = this.origin.getWorld();
        Location parentCenter = this.getCenter();

        int babySize = (int) Math.ceil(this.radius / 2.0);

        double angle = Math.random() * 2 * Math.PI;
        int distance = this.radius + babySize;

        int offsetX = (int) Math.round(Math.cos(angle) * distance);
        int offsetZ = (int) Math.round(Math.sin(angle) * distance);

        int babyX = this.origin.getBlockX() + offsetX;
        int babyZ = this.origin.getBlockZ() + offsetZ;

        int babyY = world.getHighestBlockYAt(babyX, babyZ);

        Location babyOrigin = new Location(world, babyX, babyY, babyZ);
        Cube babyCube = new Cube(babyOrigin, babySize, this.material, false);

        int totalTicks = 20 * 3600;
        int interval = 20;

        this.reproductionTask = new ReproductionTask(this, babyCube, parentCenter, babyOrigin, babySize, world, totalTicks, interval);
        this.reproductionTask.runTaskTimer(OMCPlugin.getInstance(), 0L, interval);
    }

    private void startBossBarUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(getCenter().getWorld())) {
                        double distance = player.getLocation().distanceSquared(getCenter());

                        if (distance <= 50 * 50) {
                            if (!cubeBossBar.getPlayers().contains(player)) {
                                cubeBossBar.addPlayer(player);
                            }
                        } else {
                            cubeBossBar.removePlayer(player);
                        }
                    }
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 20);
    }

    public void startEventsCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double roll = Math.random();

                if (roll < 0.5) {
                    startMagneticShock();
                } else {
                    startCorruptedBubble();
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 20L * 60 * 5, 20L * 60 * 20);
    }
}

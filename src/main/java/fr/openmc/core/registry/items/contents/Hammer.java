package fr.openmc.core.registry.items.contents;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.BlockBreakableItem;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Hammer extends CustomItem implements BlockBreakableItem {

    public static final float MAX_HARDNESS = 41.0f;
    public static final Set<UUID> BREAKERS = new HashSet<>();

    private final Material vanillaMaterial;
    @Getter
    private final int radius;
    @Getter
    private final int depth;

    public Hammer(String namespacedId, Material vanillaMaterial, int radius, int depth) {
        super(namespacedId);
        this.vanillaMaterial = vanillaMaterial;
        this.radius = radius;
        this.depth = depth;
    }

    private static BlockFace getTargetFace(Player player) {
        Location eye = player.getEyeLocation();
        RayTraceResult result = eye.getWorld().rayTraceBlocks(eye, eye.getDirection(), 10, FluidCollisionMode.NEVER);

        return result != null && result.getHitBlockFace() != null ? result.getHitBlockFace() : BlockFace.SELF;
    }

    private static Vector rotateOffset(int x, int y, int z, BlockFace face) {
        return switch (face) {
            case SOUTH -> new Vector(x, y, z);
            case NORTH -> new Vector(x, y, -z);
            case EAST -> new Vector(z, y, x);
            case WEST -> new Vector(-z, y, x);
            case UP -> new Vector(x, z, y);
            case DOWN -> new Vector(x, -z, y);
            default -> new Vector(0, 0, 0);
        };
    }

    private void breakArea(Player player, Block origin, BlockFace face, Material targetType) {
        World world = origin.getWorld();
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        int blockBroken = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = 0; dz <= depth; dz++) {

                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    Vector offset = rotateOffset(dx, dy, dz, face);
                    boolean isBroken = breakBlock(world, player, ox + offset.getBlockX(), oy + offset.getBlockY(), oz + offset.getBlockZ(), targetType);
                    if (isBroken) blockBroken++;
                }
            }
        }

        UseHammerEvent event = new UseHammerEvent(origin, this, player, blockBroken);
        Bukkit.getPluginManager().callEvent(event);
        BREAKERS.remove(player.getUniqueId());
    }

    private boolean breakBlock(World world, Player player, int x, int y, int z, Material targetType) {
        Block block = world.getBlockAt(x, y, z);

        if (block.getType() != targetType) return false;
        if (!isBreakable(block.getType())) return false;
        if (!ProtectionsManager.havePermission(player, block.getLocation(), CityPermission.BREAK)) return false;

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock != null) {
            customBlock.drop(block.getLocation());
            return customBlock.remove();
        }

        return player.breakBlock(block);
    }

    private boolean isBreakable(Material material) {
        return !material.isAir() && material.getHardness() <= MAX_HARDNESS;
    }

    @Override
    public ItemStack getVanilla() {
        return ItemStack.of(vanillaMaterial);
    }

    @Override
    public void onBlockBreak(Player player, BlockBreakEvent event) {
        if (player.getGameMode() != GameMode.SURVIVAL) return;
        if (player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD)) return;

        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        Block origin = event.getBlock();
        Material targetType = origin.getType();

        if (!isBreakable(targetType)) return;
        if (BREAKERS.contains(player.getUniqueId())) return;

        BREAKERS.add(player.getUniqueId());
        BlockFace face = getTargetFace(player).getOppositeFace();
        breakArea(player, origin, face, targetType);
    }
}

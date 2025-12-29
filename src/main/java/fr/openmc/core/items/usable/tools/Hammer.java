package fr.openmc.core.items.usable.tools;

import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.items.usable.CustomUsableItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Hammer extends CustomUsableItem {

    private static final float MAX_HARDNESS = 41.0f;

    private final Material vanillaMaterial;
    private final int radius;
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
            case NORTH, SOUTH -> new Vector(x, y, z);
            case EAST, WEST -> new Vector(z, y, x);
            case UP, DOWN -> new Vector(x, z, y);
            default -> new Vector(0, 0, 0);
        };
    }

    private void breakArea(Player player, Block origin, BlockFace face, ItemStack tool, Material targetType) {
        World world = origin.getWorld();
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -depth; dz <= depth; dz++) {

                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    Vector offset = rotateOffset(dx, dy, dz, face);
                    breakBlock(world, player, tool, ox + offset.getBlockX(), oy + offset.getBlockY(), oz + offset.getBlockZ(), targetType);
                }
            }
        }
    }

    private void breakBlock(World world, Player player, ItemStack tool, int x, int y, int z, Material targetType) {
        Block block = world.getBlockAt(x, y, z);

        if (block.getType() != targetType) return;
        if (!isBreakable(block.getType())) return;
        if (!ProtectionsManager.canInteract(player, block.getLocation())) return;

        block.breakNaturally(tool);
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

        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        Block origin = event.getBlock();
        Material targetType = origin.getType();

        if (!isBreakable(targetType)) return;

        BlockFace face = getTargetFace(player).getOppositeFace();
        breakArea(player, origin, face, tool, targetType);
    }
}

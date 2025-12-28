package fr.openmc.core.items.usable;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class Hammer extends CustomUsableItem {

    private static final float MAX_HARDNESS = 41.0f;
    private final Material vanillaMaterial;
    private final int radius;
    private final int depth;

    protected Hammer(String namespacedId,
                     Material vanillaMaterial,
                     int radius,
                     int depth) {
        super(namespacedId);
        this.vanillaMaterial = vanillaMaterial;
        this.radius = radius;
        this.depth = depth;
    }

    private static void breakArea(Player player,
                                  Block origin,
                                  BlockFace face,
                                  ItemStack tool,
                                  int radius,
                                  int depth) {
        Material targetType = origin.getType();
        if (targetType.isAir()) return;
        if (targetType.getHardness() > MAX_HARDNESS) return;

        World world = origin.getWorld();
        int baseX = origin.getX();
        int baseY = origin.getY();
        int baseZ = origin.getZ();

        IntTriConsumer apply;
        switch (face) {
            case NORTH:
            case SOUTH:
                apply = (x, y, z) -> work(world, player, tool, baseX + x, baseY + y, baseZ + z, targetType);
                break;
            case EAST:
            case WEST:
                apply = (x, y, z) -> work(world, player, tool, baseX + z, baseY + y, baseZ + x, targetType);
                break;
            case UP:
            case DOWN:
                apply = (x, y, z) -> work(world, player, tool, baseX + x, baseY + z, baseZ + y, targetType);
                break;
            default:
                return;
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -depth; dz <= depth; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    apply.accept(dx, dy, dz);
                }
            }
        }
    }

    private static void work(World world,
                             Player player,
                             ItemStack tool,
                             int x, int y, int z,
                             Material targetType) {
        Block b = world.getBlockAt(x, y, z);
        if (b.getType() != targetType) return;
        if (b.getType().getHardness() > MAX_HARDNESS) return;
        if (!ProtectionsManager.canInteract(player, b.getLocation())) return;
        b.breakNaturally(tool);
    }

    private static BlockFace getDestroyedBlockFace(Player player) {
        Location eye = player.getEyeLocation();
        RayTraceResult result = eye.getWorld().rayTraceBlocks(
                eye, eye.getDirection(), 10, FluidCollisionMode.NEVER);
        return result != null && result.getHitBlockFace() != null
                ? result.getHitBlockFace()
                : BlockFace.SELF;
    }

    @Override
    public ItemStack getVanilla() {
        return ItemStack.of(vanillaMaterial);
    }

    @Override
    public void onBlockBreak(Player player, BlockBreakEvent event) {
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;

        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir())
            return;

        Block broken = event.getBlock();
        BlockFace face = getDestroyedBlockFace(player).getOppositeFace();

        breakArea(player, broken, face, tool, radius, depth);
    }

    @FunctionalInterface
    private interface IntTriConsumer {
        void accept(int x, int y, int z);
    }
}

package fr.openmc.core.items.usable;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.Objects;

public abstract class AbstractHammer extends CustomUsableItem {

    private final Material vanillaMaterial;
    private final int radius;   // ↔ et ↕ – demi‑largeur  (1 ⇒ 3 blocs)
    private final int depth;

    protected AbstractHammer(String namespacedId,
                             Material vanillaMaterial,
                             int radius,
                             int depth) {
        super(namespacedId);
        this.vanillaMaterial = vanillaMaterial;
        this.radius = radius;
        this.depth  = depth;
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
        if (tool == null || tool.getType().isAir())
            return;

        Block broken = event.getBlock();
        BlockFace face = getDestroyedBlockFace(player).getOppositeFace();

        breakArea(player, broken, face, tool, radius, depth);
    }

    private static void breakArea(Player player,
                                  Block origin,
                                  BlockFace face,
                                  ItemStack tool,
                                  int radius,
                                  int depth) {

        Material targetType = origin.getType();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -depth;  dz <= depth;  dz++) {

                    int ox = 0, oy = 0, oz = 0;
                    switch (face) {
                        case NORTH:
                        case SOUTH:
                            ox = dx; oy = dy; oz = dz;
                            break;
                        case EAST:
                        case WEST:
                            ox = dz; oy = dy; oz = dx;
                            break;
                        case UP:
                        case DOWN:
                            ox = dx; oy = dz; oz = dy;
                            break;
                        default:
                            break;
                    }

                    Block b = origin.getRelative(ox, oy, oz);

                    if (Objects.equals(b, origin))
                        continue;
                    if (!ProtectionsManager.canInteract(player, b.getLocation()))
                        continue;
                    if (b.getType() != targetType)
                        continue;
                    if (b.getType().getHardness() > 41)
                        continue;

                    b.breakNaturally(tool);
                }
            }
        }
    }

    private static BlockFace getDestroyedBlockFace(Player player) {
        Location eye = player.getEyeLocation();
        RayTraceResult result = eye.getWorld().rayTraceBlocks(
                eye, eye.getDirection(), 10, FluidCollisionMode.NEVER);
        return result != null && result.getHitBlockFace() != null
                ? result.getHitBlockFace()
                : BlockFace.SELF;
    }
}

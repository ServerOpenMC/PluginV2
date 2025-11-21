package fr.openmc.core.utils.world;

import lombok.Getter;
import org.bukkit.block.BlockFace;

public enum Yaw {

    NORTH(180),
    EAST(270),
    SOUTH(0),
    WEST(90);

    public Yaw getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EAST;
        };
    }

    public BlockFace toBlockFace() {
        return switch (this) {
            case NORTH -> BlockFace.NORTH;
            case EAST -> BlockFace.EAST;
            case SOUTH -> BlockFace.SOUTH;
            case WEST -> BlockFace.WEST;
        };
    }
    
    @Getter
    final float playerYaw;
    
    Yaw (float playerYaw) {
        this.playerYaw = playerYaw;
    }
}

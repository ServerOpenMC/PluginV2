package fr.openmc.core.features.itemsadder.elevator;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.block.Block;

public class ElevatorIAUtils {

    public static CustomBlock getCustomBlock(Block block) {
        return CustomBlock.byAlreadyPlaced(block);
    }

    public static boolean isElevator(CustomStack item) {
        if (item == null) return false;
        return ElevatorManager.isElevator(item.getNamespacedID());
    }

}

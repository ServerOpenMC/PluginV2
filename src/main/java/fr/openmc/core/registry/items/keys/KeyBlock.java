package fr.openmc.core.registry.items.keys;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.registry.items.CustomItem;
import org.bukkit.block.BlockType;

public record KeyBlock(BlockType blockType, CustomBlock customBlock) {

    public static KeyBlock vanilla(BlockType type) {
        return new KeyBlock(type, null);
    }

    public static KeyBlock custom(CustomBlock block) {
        return new KeyBlock(null, block);
    }

    public static KeyBlock custom(CustomItem item) {
        return new KeyBlock(null, item.getCustomBlock());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof BlockType otherBlockType) {
            return this.blockType.equals(otherBlockType);
        } else if (object instanceof CustomBlock otherCustomBlock) {
            return this.customBlock.getNamespacedID().equals(otherCustomBlock.getNamespacedID());
        } else if (object instanceof CustomItem otherCustomItem) {
            return this.customBlock.getNamespacedID().equals(otherCustomItem.getId());
        }
        return false;
    }
}
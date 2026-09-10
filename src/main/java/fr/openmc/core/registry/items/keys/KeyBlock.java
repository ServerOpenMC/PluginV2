package fr.openmc.core.registry.items.keys;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;

public final class KeyBlock {
    @Getter
    private final BlockType blockType;
    @Getter
    private final CustomBlock customBlock;
    private final String id;

    private KeyBlock(BlockType blockType, CustomBlock customBlock, String id) {
        this.blockType = blockType;
        this.customBlock = customBlock;
        this.id = id;
    }

    public static KeyBlock vanilla(BlockType type) {
        return new KeyBlock(type, null, "vanilla:" + type.getKey());
    }

    public static KeyBlock custom(CustomBlock block) {
        return new KeyBlock(null, block, "custom:" + block.getNamespacedID());
    }

    public static KeyBlock custom(CustomItem item) {
        return new KeyBlock(null, item.getCustomBlock(), "custom:" + item.getId());
    }

    public static KeyBlock fromBlock(Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock != null) {
            return custom(customBlock);
        }
        return vanilla(block.getType().asBlockType());
    }

    public CustomItem getCustomItem() {
        return OMCRegistry.CUSTOM_ITEMS.get(customBlock.getNamespacedID()).orElse(null);
    }

    public boolean isVanilla() {
        return blockType != null;
    }

    public boolean isCustom() {
        return customBlock != null;
    }

    public Component name() {
        if (isVanilla()) return Component.translatable(blockType.translationKey());
        else {
            CustomItem item = getCustomItem();
            ItemStack itemStack = item.getBest();
            if (itemStack.getItemMeta().hasItemName()) {
                return itemStack.getItemMeta().itemName();
            }
            return itemStack.displayName();
        }
    }

    public boolean matches(Block block) {
        if (block == null) return false;

        CustomBlock placed = CustomBlock.byAlreadyPlaced(block);
        if (placed != null && isCustom()) return customBlock.getNamespacedID().equals(placed.getNamespacedID());

        return blockType == block.getType().asBlockType();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof KeyBlock other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "KeyBlock[" + id + "]";
    }
}
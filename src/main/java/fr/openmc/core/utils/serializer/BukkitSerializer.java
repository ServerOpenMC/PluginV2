package fr.openmc.core.utils.serializer;

import org.bukkit.inventory.ItemStack;

public class BukkitSerializer {
    public static byte[] serializeItemStacks(ItemStack[] inv) {
        return ItemStack.serializeItemsAsBytes(inv);
    }

    public static ItemStack[] deserializeItemStacks(byte[] b) {
        return ItemStack.deserializeItemsFromBytes(b);
    }
}
package fr.openmc.core.utils.serializer;

import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class BukkitSerializer {
    public static byte[] serializeItemStacks(ItemStack[] inv) throws IOException {
        return inv != null ? ItemStack.serializeItemsAsBytes(inv) : new byte[0];
    }

    public static ItemStack[] deserializeItemStacks(byte[] b) {
        if (b == null || b.length == 0) {
            return new ItemStack[0];
        }
        return ItemStack.deserializeItemsFromBytes(b);
    }
}
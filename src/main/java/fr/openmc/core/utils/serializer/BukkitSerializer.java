package fr.openmc.core.utils.serializer;

import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class BukkitSerializer {
    public static byte[] serializeItemStacks(ItemStack[] inv) throws IOException {
        return ItemStack.serializeItemsAsBytes(inv);
    }

    public static ItemStack[] deserializeItemStacks(byte[] b) {
        return ItemStack.deserializeItemsFromBytes(b);
    }
}
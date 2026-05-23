package fr.openmc.core.utils.bukkit.serializer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public class BukkitSerializer {
    public static byte[] serializeItemStacks(ItemStack[] inv) throws IOException {
        return inv != null ? ItemStack.serializeItemsAsBytes(inv) : new byte[0];
    }

    public static ItemStack[] deserializeItemStacks(byte[] b) {
        if (b == null || b.length == 0)
            return new ItemStack[0];

        return ItemStack.deserializeItemsFromBytes(b);
    }

    public static String playerInventoryToBase64(PlayerInventory inv) {
        byte[] bytes = ItemStack.serializeItemsAsBytes(Arrays.asList(inv.getContents()));
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String playerInventoryToBase64(ItemStack[] contents) {
        if (contents == null || contents.length == 0)
            return "";

        byte[] bytes = ItemStack.serializeItemsAsBytes(Arrays.asList(contents));
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void playerInventoryFromBase64(PlayerInventory inv, String data) {
        byte[] bytes = Base64.getDecoder().decode(data);
        Collection<ItemStack> items = List.of(ItemStack.deserializeItemsFromBytes(bytes));
        inv.setContents(items.toArray(new ItemStack[0]));
    }
}
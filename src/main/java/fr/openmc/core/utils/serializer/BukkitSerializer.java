package fr.openmc.core.utils.serializer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public static String playerInventoryToBase64(PlayerInventory inv) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(inv.getContents());

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save player inventory.", e);
        }
    }

    public static String playerInventoryToBase64(ItemStack[] contents) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(contents);

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save player inventory.", e);
        }
    }

    public static void playerInventoryFromBase64(PlayerInventory inv, String data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            ItemStack[] contents = (ItemStack[]) dataInput.readObject();

            inv.setContents(contents);
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
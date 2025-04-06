package fr.openmc.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ItemUtils {
    /**
     * Return a {@link TranslatableComponent} from a {@link ItemStack}
     * @param stack ItemStack that get translate
     * @return a {@link TranslatableComponent} that can be translated by client
     */
    public static TranslatableComponent getItemTranslation(ItemStack stack) {
        return Component.translatable(Objects.requireNonNullElse(
                stack.getType().getItemTranslationKey(),
                "block.minecraft.stone"
        ));
    }

    /**
     * Return a {@link TranslatableComponent} from a {@link Material}
     * @param material Material that get translate
     * @return a {@link TranslatableComponent} that can be translated by client
     */
    public static TranslatableComponent getItemTranslation(Material material) {
        return getItemTranslation(new ItemStack(material));
    }

    public static List<ItemStack> splitAmountIntoStack(ItemStack items) {
        int amount = items.getAmount();

        List<ItemStack> stacks = new ArrayList<>();
        while (amount > 64) {
            ItemStack item = items.clone();
            item.setAmount(64);
            stacks.add(item);

            amount -= 64;
        }

        if (amount > 0) {
            ItemStack item = items.clone();
            item.setAmount(amount);
            stacks.add(item);
        }

        return stacks;
    }

    public static int getNumberItemToStack(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();
        int numberitemtostack = 0;

        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack != null && stack.isSimilar(item)) {
                numberitemtostack = 64 - stack.getAmount();
            }
        }
        return numberitemtostack;
    }

    public static int getSlotNull(Player player) {
        Inventory inventory = player.getInventory();

        int slot = 0;

        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack == null) {
                slot++;
            }
        }

        return slot;
    }


    // IMPORT FROM AXENO
    public static boolean hasEnoughItems(Player player, Material item, int amount) {
        int totalItems = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (ItemStack is : contents) {
            if (is != null && is.getType() == item) {
                totalItems += is.getAmount();
            }
        }

        if (amount == 0) return false;
        return totalItems >= amount;
    }

    public static boolean hasAvailableSlot(Player player) {
        Inventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();

        for (int i = 0; i < contents.length; i++) {
            // on ne vérifie pas la main secondaire et l'armure
            if (i >= 36 && i <= 40) continue;

            if (contents[i] == null) {
                return true;
            }
        }
        return false;
    }

    public static void removeItemsFromInventory(Player player, Material item, int quantity) {
        ItemStack[] contents = player.getInventory().getContents();
        int remaining = quantity;

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == item) {
                int stackAmount = stack.getAmount();
                if (stackAmount <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= stackAmount;
                } else {
                    stack.setAmount(stackAmount - remaining);
                    remaining = 0;
                }
            }
        }
    }

    public static Material getSignType(Player player) {
        HashMap<Biome, Material> biomeToSignType = new HashMap<>();
        biomeToSignType.put(Biome.BAMBOO_JUNGLE, Material.BAMBOO_SIGN);
        biomeToSignType.put(Biome.BIRCH_FOREST, Material.BIRCH_SIGN);
        biomeToSignType.put(Biome.OLD_GROWTH_BIRCH_FOREST, Material.BIRCH_SIGN);
        biomeToSignType.put(Biome.JUNGLE, Material.JUNGLE_SIGN);
        biomeToSignType.put(Biome.SPARSE_JUNGLE, Material.JUNGLE_SIGN);
        biomeToSignType.put(Biome.PALE_GARDEN, Material.PALE_OAK_SIGN);
        biomeToSignType.put(Biome.CHERRY_GROVE, Material.CHERRY_SIGN);
        biomeToSignType.put(Biome.CRIMSON_FOREST, Material.CRIMSON_SIGN);
        biomeToSignType.put(Biome.WARPED_FOREST, Material.WARPED_SIGN);
        biomeToSignType.put(Biome.MANGROVE_SWAMP, Material.MANGROVE_SIGN);
        biomeToSignType.put(Biome.SAVANNA, Material.ACACIA_SIGN);
        biomeToSignType.put(Biome.SAVANNA_PLATEAU, Material.ACACIA_SIGN);
        biomeToSignType.put(Biome.WINDSWEPT_SAVANNA, Material.ACACIA_SIGN);
        biomeToSignType.put(Biome.DARK_FOREST, Material.DARK_OAK_SIGN);
        biomeToSignType.put(Biome.TAIGA, Material.SPRUCE_SIGN);
        biomeToSignType.put(Biome.OLD_GROWTH_PINE_TAIGA, Material.SPRUCE_SIGN);
        biomeToSignType.put(Biome.SNOWY_TAIGA, Material.SPRUCE_SIGN);
        biomeToSignType.put(Biome.OLD_GROWTH_SPRUCE_TAIGA, Material.SPRUCE_SIGN);

        Biome playerBiome = player.getWorld().getBiome(player.getLocation());

        return biomeToSignType.getOrDefault(playerBiome, Material.OAK_SIGN);
    }
}

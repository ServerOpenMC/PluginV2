package fr.openmc.core.features.dream.models.registry;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("UnstableApiUsage")
public abstract class DreamEnchantment {
    public abstract Key getKey();

    public abstract Component getName();

    public abstract TagKey<ItemType> getSupportedItems();

    public abstract int getMaxLevel();

    public abstract int getWeight();

    public abstract int getAnvilCost();

    public abstract EnchantmentRegistryEntry.EnchantmentCost getMinimumCost();

    public abstract EnchantmentRegistryEntry.EnchantmentCost getMaximalmCost();

    public DreamItem getEnchantedBookItem(int level) {
        return new DreamItem(getKey().asMinimalString() + level) {
            @Override
            public @NonNull ItemStack getVanilla() {
                return getEnchantedBook(level);
            }

            @Override
            public DreamRarity getRarity() {
                return DreamRarity.EPIC;
            }

            @Override
            public boolean isTransferable() {
                return true;
            }

            @Override
            public ItemStack getTransferableItem() {
                return getEnchantedBook(level);
            }

            private ItemStack getEnchantedBook(int level) {
                ItemStack bookEnchanted = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) bookEnchanted.getItemMeta();

                Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess
                        .registryAccess()
                        .getRegistry(RegistryKey.ENCHANTMENT);

                Enchantment enchantment = enchantmentRegistry.getOrThrow(RegistryKey.ENCHANTMENT.typedKey(getKey()));

                meta.addStoredEnchant(enchantment, level, false);
                bookEnchanted.setItemMeta(meta);
                return bookEnchanted;
            }
        };
    }
}

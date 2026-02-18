package fr.openmc.core.registry.enchantments;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.registry.items.CustomItem;
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

@SuppressWarnings("UnstableApiUsage")
public abstract class CustomEnchantment {

    public abstract Key getKey();

    public abstract Component getName();

    public abstract TagKey<ItemType> getSupportedItems();

    public abstract int getMaxLevel();

    public abstract int getWeight();

    public abstract int getAnvilCost();

    public abstract EnchantmentRegistryEntry.EnchantmentCost getMinimumCost();

    public abstract EnchantmentRegistryEntry.EnchantmentCost getMaximalmCost();

    public CustomItem getEnchantedBookItem(int level) {
        return new CustomItem(getKey().asMinimalString() + level) {
            @Override
            public ItemStack getVanilla() {
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

    public Enchantment getEnchantment() {
        Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess
                .registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT);

        return enchantmentRegistry.getOrThrow(RegistryKey.ENCHANTMENT.typedKey(getKey()));
    }
}

package fr.openmc.core.features.tickets.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record LootItem(ItemStack material, String displayName, List<Component> lore, double chance, int maxRewards, List<ItemStack> rewards) {
    public LootItem(Material material, @NotNull String displayName, List<Component> lore, double chance, int maxRewards, List<ItemStack> rewards) {
        this(new ItemStack(material), displayName, lore, chance, maxRewards, rewards);
    }
    public LootItem(Material material, @NotNull String displayName, List<Component> lore, double chance, List<ItemStack> rewards) {
        this(new ItemStack(material), displayName, lore, chance, -1, rewards);
    }
}
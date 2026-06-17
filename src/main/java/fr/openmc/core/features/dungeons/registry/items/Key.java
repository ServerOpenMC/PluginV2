package fr.openmc.core.features.dungeons.registry.items;

import fr.openmc.core.features.dungeons.Rarity;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemMeta;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class Key extends CustomItem {

    private final int level;
    private final Rarity rarity;


    public Key(int level, Rarity rarity) {
        super("omc_dungeons:key_level_" + level + "_" + rarity.name().toLowerCase());
        this.level = level;
        this.rarity = rarity;
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.TRIPWIRE_HOOK);
    }
}

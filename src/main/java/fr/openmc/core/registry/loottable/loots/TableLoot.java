package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.loottable.CustomLootTable;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@Getter
public class TableLoot implements CustomLoot, RepresentedItem {
    @Setter
    private double chance;
    private final ItemStack item;
    private final CustomLootTable lootTable;
    private final boolean giveRewards;

    public TableLoot(CustomLootTable lootTable, double chance, boolean giveRewards) {
        this.chance = chance;
        this.lootTable = lootTable;
        this.giveRewards = giveRewards;
        this.item = null;
    }

    public TableLoot(CustomLootTable lootTable, Material item, double chance, boolean giveRewards) {
        this(lootTable, ItemStack.of(item), chance, giveRewards);
    }

    public TableLoot(CustomLootTable lootTable, ItemStack item, double chance, boolean giveRewards) {
        this.chance = chance;
        this.lootTable = lootTable;
        this.giveRewards = giveRewards;
        this.item = item;
    }

    public TableLoot(CustomLootTable lootTable, CustomItem item, double chance, boolean giveRewards) {
        this(lootTable, item.getBest(), chance, giveRewards);
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return item;
    }

    @Override
    public Component getDisplayText() {
        return lootTable.getName();
    }

    @Override
    public Set<CustomLoot> run(Player receiver) {
        if (this.giveRewards)
            return Set.copyOf(lootTable.rollLoots(receiver));
        else
            return Set.copyOf(lootTable.rollLoots());
    }
}
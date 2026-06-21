package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.loottable.CustomLootTable;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Set;

@Getter
public class TableLoot implements CustomLoot {
    @Setter
    private double chance;
    private final CustomLootTable lootTable;
    private final boolean giveRewards;

    public TableLoot(CustomLootTable lootTable, double chance, boolean giveRewards) {
        this.chance = chance;
        this.lootTable = lootTable;
        this.giveRewards = giveRewards;
    }

    @Override
    public Component getDisplayText() {
        return null;
    }

    @Override
    public Set<CustomLoot> run(Player receiver) {
        return Set.copyOf(lootTable.rollLoots(receiver, this.giveRewards));
    }
}
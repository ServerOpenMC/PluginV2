package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.loottable.CustomLootTable;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Getter
public class TableLoot implements CustomLoot {
    private final double chance;
    private final CustomLootTable lootTable;

    public TableLoot(CustomLootTable lootTable, double chance) {
        this.chance = chance;
        this.lootTable = lootTable;
    }

    @Override
    public Component getDisplayText() {
        return null;
    }

    @Override
    public void run(Player receiver) {
        lootTable.rollLoots(receiver);
    }
}
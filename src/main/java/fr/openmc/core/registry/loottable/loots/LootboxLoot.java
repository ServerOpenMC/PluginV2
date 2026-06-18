package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.lootboxes.CustomLootbox;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class LootboxLoot implements CustomLoot {
    private final double chance;
    private final CustomLootbox lootbox;

    public LootboxLoot(CustomLootbox lootbox, double chance) {
        this.chance = chance;
        this.lootbox = lootbox;
    }

    @Override
    public void run(Player receiver) {
        lootbox.open(receiver);
    }
}
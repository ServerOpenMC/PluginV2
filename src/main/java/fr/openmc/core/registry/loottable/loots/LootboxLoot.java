package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.lootboxes.CustomLootbox;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

@Getter
public class LootboxLoot implements CustomLoot {
    private final double chance;
    private final CustomLootbox lootbox;

    public LootboxLoot(CustomLootbox lootbox, double chance) {
        this.chance = chance;
        this.lootbox = lootbox;
    }

    @Override
    public Component getDisplayText() {
        return lootbox.getName();
    }

    @Override
    public Set<CustomLoot> run(Player receiver) {
        lootbox.open(receiver);
        return Collections.singleton(this);
    }
}
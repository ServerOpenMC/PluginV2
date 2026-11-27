package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.loottable.LootReward;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;

@Getter
public class IdLoot implements CustomLoot {
    @Setter
    private double chance;
    private final String id;

    public IdLoot(String id) {
        this.chance = 1;
        this.id = id;
    }

    public IdLoot(String id, double chance) {
        this.chance = chance;
        this.id = id;
    }

    @Override
    public Component getDisplayText() {
        return Component.text(this.id);
    }

    @Override
    public LootReward run(OfflinePlayer receiver) {
        return null;
    }
}
package fr.openmc.core.registry.loottable.loots;


import fr.openmc.core.registry.loottable.LootReward;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Getter
public class EmptyLoot implements CustomLoot {
    @Setter
    private double chance;

    public EmptyLoot(double chance) {
        this.chance = chance;
    }

    @Override
    public Component getDisplayText() {
        return Component.text("Empty");
    }

    @Override
    public LootReward run(Player receiver) {
        return null;
    }
}
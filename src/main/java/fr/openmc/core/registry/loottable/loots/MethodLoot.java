package fr.openmc.core.registry.loottable.loots;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Getter
public class MethodLoot implements CustomLoot {
    private final double chance;
    private final Consumer<Player> receiverAction;

    public MethodLoot(Consumer<Player> receiverAction, double chance) {
        this.chance = chance;
        this.receiverAction = receiverAction;
    }

    @Override
    public void run(Player receiver) {
        receiverAction.accept(receiver);
    }
}

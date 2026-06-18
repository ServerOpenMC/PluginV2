package fr.openmc.core.registry.loottable.loots;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Getter
public class MethodLoot implements CustomLoot {
    private final Component text;
    private final double chance;
    private final Consumer<Player> receiverAction;

    public MethodLoot(Component text, Consumer<Player> receiverAction, double chance) {
        this.text = text;
        this.chance = chance;
        this.receiverAction = receiverAction;
    }

    @Override
    public Component getDisplayText() {
        return text;
    }

    @Override
    public void run(Player receiver) {
        receiverAction.accept(receiver);
    }
}

package fr.openmc.core.registry.loottable.loots;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.registry.loottable.LootReward;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.function.Consumer;

@Getter
public class MethodLoot implements CustomLoot, RepresentedItem {
    private final ItemStack representativeItem;
    private final Component text;
    @Setter
    private double chance;
    private final Consumer<OMCPlayer> receiverAction;

    public MethodLoot(ItemStack representativeItem, Component text, Consumer<OMCPlayer> receiverAction, double chance) {
        this.representativeItem = representativeItem;
        this.text = text;
        this.chance = chance;
        this.receiverAction = receiverAction;
    }

    @Override
    public Component getDisplayText() {
        return text;
    }

    @Override
    public LootReward run(OMCPlayer receiver) {
        receiverAction.accept(receiver);
        return LootReward.loots(Collections.singleton(this));
    }
}

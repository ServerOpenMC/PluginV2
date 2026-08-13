package fr.openmc.core.registry.loottable;

import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;

public record LootReward(List<CustomLoot> loots) {
    public static LootReward loots(Collection<CustomLoot> newLoots) {
        return new LootReward(newLoots.stream().toList());
    }

    public LootReward copy() {
        return new LootReward(List.copyOf(loots));
    }

    public Component buildComponent() {
        Component component = null;

        for (CustomLoot loot : loots) {
            int amount = -1;
            if (loot instanceof ItemLoot itemLoot) {
                amount = itemLoot.getRepresentativeItem().getAmount();
            }
            Component lootComponent = loot.buildLootComponent(amount);
            if (lootComponent == null) continue;

            if (component == null)
                component = lootComponent;
            else
                component = component.appendNewline().append(lootComponent);
        }

        return component;
    }
}

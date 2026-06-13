package fr.openmc.core.registry.lootboxes;

import fr.openmc.api.menulib.utils.InventorySize;

import java.util.List;

public record LootboxOptions(InventorySize menuSize, int animationsTickDuration, List<Integer> displaySlots, int rewardSlot, String textureMenu) {
    public LootboxOptions(InventorySize menuSize, int animationsTickDuration, List<Integer> displaySlots, int rewardSlot) {
        this(menuSize, animationsTickDuration, displaySlots, rewardSlot, null);
    }
}

package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.lootboxes.CustomLootbox;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

@Getter
public class LootboxLoot implements CustomLoot, RepresentedItem {
    @Setter
    private double chance;
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

    @Override
    public ItemStack getRepresentativeItem() {
        return lootbox.getItemDisplayed();
    }
}
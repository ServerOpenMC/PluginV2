package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.reward;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EmptyReward implements PointReward{
    @Override public void giveReward() {}

    @Override
    public ItemStack getItemMenuPreview() {
        return new ItemStack(Material.BARRIER);
    }

    @Override
    public Component getRewardName() {
        return Component.empty();
    }

    @Override
    public List<Component> getRewardLore() {
        return List.of(Component.empty());
    }
}

package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.reward;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PointReward {
    void giveReward();

    ItemStack getItemMenuPreview();

    Component getRewardName();
    List<Component> getRewardLore();
}

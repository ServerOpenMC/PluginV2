package fr.openmc.core.registry.loottable.loots;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface CustomLoot {
    Component getDisplayText();
    double getChance();
    void setChance(double chance);
    Set<CustomLoot> run(Player receiver);

    default ItemStack getRepresentativeItem() {
        if (this instanceof RepresentedItem representedItem) {
            return representedItem.getRepresentativeItem();
        }
        return null;
    }
}
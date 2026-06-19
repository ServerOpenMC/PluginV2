package fr.openmc.core.registry.loottable.loots;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Set;

public interface CustomLoot {
    Component getDisplayText();
    double getChance();
    Set<CustomLoot> run(Player receiver);
}
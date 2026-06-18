package fr.openmc.core.registry.loottable.loots;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface CustomLoot {
    Component getDisplayText();
    double getChance();
    void run(Player receiver);
}
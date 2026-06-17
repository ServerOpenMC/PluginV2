package fr.openmc.core.registry.loottable.loots;

import org.bukkit.entity.Player;

public interface CustomLoot {
    double getChance();
    void run(Player receiver);
}
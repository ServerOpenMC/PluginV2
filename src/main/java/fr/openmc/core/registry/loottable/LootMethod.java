package fr.openmc.core.registry.loottable;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public record LootMethod(Consumer<Player> receiver) implements CustomLoot {

}

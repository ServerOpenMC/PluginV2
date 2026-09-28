package fr.openmc.core.registry.worldtemplates.interfaces;

import org.bukkit.GameRule;
import org.bukkit.World;

import java.util.Map;

public interface HasGamerules {
    Map<GameRule<?>, Object> getGamerules();

    @SuppressWarnings("unchecked")
    public static <T> void applyRule(World world, GameRule<T> rule, Object value) {
        world.setGameRule(rule, (T) value);
    }
}

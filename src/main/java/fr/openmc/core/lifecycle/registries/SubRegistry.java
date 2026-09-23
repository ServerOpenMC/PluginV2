package fr.openmc.core.lifecycle.registries;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public abstract class SubRegistry<K, V> implements LifecycleRegistry {

    @Getter
    private final Set<V> registry = new HashSet<>();

    public abstract KeyedRegistry<K, ? super V> getParentRegistry();

    public <T extends V> T register(T value) {
        registry.add(value);
        getParentRegistry().register(value);
        return value;
    }
}
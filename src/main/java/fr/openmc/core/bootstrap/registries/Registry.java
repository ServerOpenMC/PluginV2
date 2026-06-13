package fr.openmc.core.bootstrap.registries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Registry<K, V> implements LifecycleRegistry {

    protected final Map<K, V> entries = new HashMap<>();

    public V register(K key, V value) {
        entries.put(key, value);
        return value;
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(entries.get(key));
    }

    public V getOrThrow(K key) {
        return get(key).orElseThrow(() -> new IllegalArgumentException("No entry found for key: " + key));
    }

    public Collection<K> keys() {
        return entries.keySet();
    }

    public Collection<V> values() {
        return entries.values();
    }
}

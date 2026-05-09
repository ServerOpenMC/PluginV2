package fr.openmc.core.bootstrap.registries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Registry<K, V> implements LifecycleRegistry {

    protected final Map<K, V> entries = new HashMap<>();

    public void register(K key, V value) {
        entries.put(key, value);
    }

    public V get(K key) {
        return entries.get(key);
    }

    public Collection<K> keys() {
        return entries.keySet();
    }

    public Collection<V> values() {
        return entries.values();
    }
}

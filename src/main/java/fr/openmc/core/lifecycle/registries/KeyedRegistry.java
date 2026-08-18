package fr.openmc.core.lifecycle.registries;

public interface KeyedRegistry<K, V> {
    K key(V registryObject);

    V register(K key, V value);

    default <T extends V> T register(T value) {
        register(key(value), value);
        return value;
    }

    default void register(V... values) {
        for (V value : values) {
            register(value);
        }
    }

    default void register(Iterable<V> values) {
        for (V value : values) {
            register(value);
        }
    }
}
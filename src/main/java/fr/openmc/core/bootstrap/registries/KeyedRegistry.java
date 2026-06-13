package fr.openmc.core.bootstrap.registries;

public interface KeyedRegistry<K, V> {
    K key(V registryObject);

    V register(K key, V value);

    default V register(V value) {
        return register(key(value), value);
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

package fr.openmc.core.bootstrap.registries;

public interface KeyedRegistry<K, V> {
    K key(V registryObject);

    void register(K key, V value);

    default void register(V value) {
        register(key(value), value);
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

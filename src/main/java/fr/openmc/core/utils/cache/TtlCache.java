package fr.openmc.core.utils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TtlCache<K, V> {
    private record Entry<V>(V value, long expiresAt) {
    }

    private final Map<K, Entry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public TtlCache(long ttl, TimeUnit unit) {
        this.ttlMillis = unit.toMillis(ttl);
    }

    public V get(K key) {
        Entry<V> entry = cache.get(key);
        if (entry == null || System.currentTimeMillis() > entry.expiresAt()) return null;
        return entry.value();
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public void put(K key, V value) {
        cache.put(key, new Entry<>(value, System.currentTimeMillis() + ttlMillis));
    }

    public void invalidate(K key) {
        cache.remove(key);
    }

    public V getOrCompute(K key, Function<K, V> loader) {
        V cached = get(key);
        if (cached != null) return cached;

        V computed = loader.apply(key);
        if (computed != null) put(key, computed);
        return computed;
    }
}

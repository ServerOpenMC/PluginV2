package fr.openmc.core.utils;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class Queue<K, V> {

    private final LinkedHashMap<K, V> queue;

    public Queue(int size) {
        this.queue = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                return size() > size;
            }
        };
    }

    public void add(K key, V value) {
        queue.put(key, value);
    }

    public void remove(K key) {
        queue.remove(key);
    }

    public V get(K key) {
        return queue.get(key);
    }

}

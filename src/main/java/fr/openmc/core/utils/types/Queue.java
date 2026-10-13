package fr.openmc.core.utils.types;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class Queue<K, V> {

    private final LinkedHashMap<K, V> queueMap;

    public Queue(int size) {
        this.queueMap = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                return size() > size;
            }
        };
    }

    public void add(K key, V value) {
        queueMap.put(key, value);
    }

    public void remove(K key) {
        queueMap.remove(key);
    }

    public V get(K key) {
        return queueMap.get(key);
    }

}

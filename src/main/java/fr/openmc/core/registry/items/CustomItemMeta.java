package fr.openmc.core.registry.items;

import java.util.HashMap;
import java.util.Map;

public class CustomItemMeta {
    private final Map<String, Object> metaMap = new HashMap<>();

    public CustomItemMeta(String id) {
        add("id", id);
    }

    public String getId() {
        return (String) metaMap.get("id");
    }

    public void add(String key, Object object) {
        metaMap.put(key, object);
    }

    public Object get(String key) {
        return metaMap.get(key);
    }
}

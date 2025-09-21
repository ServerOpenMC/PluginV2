package fr.openmc.core.utils;

import java.util.HashMap;
import java.util.Map;

public class YmlUtils {
    public static Map<String, Object> deepCopy(Map<?, ?> original) {
        Map<String, Object> copy = new HashMap<>();
        for (Map.Entry<?, ?> entry : original.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(entry.getKey().toString(), entry.getValue());
            }
        }
        return copy;
    }
}

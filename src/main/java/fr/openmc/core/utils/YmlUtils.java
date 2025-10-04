package fr.openmc.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YmlUtils {
    public static Object deepCopyObject(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> copy = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    copy.put(entry.getKey().toString(), deepCopyObject(entry.getValue()));
                }
            }
            return copy;
        } else if (value instanceof List<?> list) {
            List<Object> copy = new ArrayList<>();
            for (Object item : list) {
                copy.add(deepCopyObject(item));
            }
            return copy;
        } else {
            return value;
        }
    }

    public static Map<String, Object> deepCopy(Map<?, ?> original) {
        return (Map<String, Object>) deepCopyObject(original);
    }
}

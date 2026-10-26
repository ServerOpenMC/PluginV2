package fr.openmc.core.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YmlUtils {
    public static Map<String, Object> loadYml(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return new Yaml().load(in);
        } catch (Exception e) {
            throw new IOException("Erreur lors de la lecture du fichier yml " + path, e);
        }
    }

    public static String getString(Map<?, ?> map, String key, String def) {
        if (map == null) return def;
        Object obj = map.get(key);
        if (obj == null) return def;
        if (obj instanceof String s) return s;
        return String.valueOf(obj);
    }

    public static Integer getInt(Map<?, ?> map, String key, Integer def) {
        if (map == null) return def;
        Object obj = map.get(key);
        if (obj == null) return def;
        if (obj instanceof Number n) return n.intValue();
        return Integer.parseInt(obj.toString());
    }

    public static Boolean getBool(Map<?, ?> map, String key, Boolean def) {
        if (map == null) return def;
        Object obj = map.get(key);
        if (obj == null) return def;
        if (obj instanceof Boolean b) return b;
        return Boolean.parseBoolean(obj.toString());
    }

    public static Double getDouble(Map<?, ?> map, String key, Double def) {
        if (map == null) return def;
        Object obj = map.get(key);
        if (obj == null) return def;
        if (obj instanceof Number n) return n.doubleValue();
        return Double.parseDouble(obj.toString());
    }

    public static Map<String, String> getStringMap(Map<?, ?> map, String key) {
        Map<?, ?> section = getMap(map, key);
        if (section == null) return null;

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<?, ?> e : section.entrySet()) {
            result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
        }
        return result;
    }

    public static Map<?, ?> getMap(Map<?, ?> map, String key) {
        if (map == null) return null;
        return map.get(key) instanceof Map<?, ?> m ? m : null;
    }

    public static List<String> getStringList(Map<?, ?> map, String key) {
        if (map == null) return List.of();
        return map.get(key) instanceof List<?> l
                ? l.stream().map(String::valueOf).toList()
                : List.of();
    }

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

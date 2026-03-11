package fr.openmc.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class YmlUtilsTest {

    @Test
    @DisplayName("Deep copy simple map")
    void testDeepCopy_SimpleMap() {
        Map<String, Object> original = new HashMap<>();
        original.put("key1", "value1");
        original.put("key2", 42);

        Map<String, Object> copy = YmlUtils.deepCopy(original);

        Assertions.assertEquals(original, copy);
        Assertions.assertNotSame(original, copy);
    }

    @Test
    @DisplayName("Deep copy nested map")
    void testDeepCopy_NestedMap() {
        Map<String, Object> inner = new HashMap<>();
        inner.put("nested", "value");

        Map<String, Object> original = new HashMap<>();
        original.put("outer", inner);

        Map<String, Object> copy = YmlUtils.deepCopy(original);

        Assertions.assertEquals("value", ((Map<?, ?>) copy.get("outer")).get("nested"));

        inner.put("nested", "modified");
        Assertions.assertEquals("value", ((Map<?, ?>) copy.get("outer")).get("nested"));
    }

    @Test
    @DisplayName("Deep copy with list")
    void testDeepCopy_WithList() {
        List<Object> list = new ArrayList<>();
        list.add("a");
        list.add("b");

        Map<String, Object> original = new HashMap<>();
        original.put("items", list);

        Map<String, Object> copy = YmlUtils.deepCopy(original);

        list.add("c");
        List<?> copiedList = (List<?>) copy.get("items");
        Assertions.assertEquals(2, copiedList.size());
    }

    @Test
    @DisplayName("Deep copy primitives are preserved")
    void testDeepCopyObject_Primitives() {
        Assertions.assertEquals("hello", YmlUtils.deepCopyObject("hello"));
        Assertions.assertEquals(42, YmlUtils.deepCopyObject(42));
        Assertions.assertEquals(3.14, YmlUtils.deepCopyObject(3.14));
        Assertions.assertTrue((Boolean) YmlUtils.deepCopyObject(true));
    }

    @Test
    @DisplayName("Deep copy empty map")
    void testDeepCopy_EmptyMap() {
        Map<String, Object> copy = YmlUtils.deepCopy(new HashMap<>());
        Assertions.assertTrue(copy.isEmpty());
    }

    @Test
    @DisplayName("Deep copy null key is skipped")
    void testDeepCopy_NullKey() {
        Map<String, Object> original = new HashMap<>();
        original.put(null, "value");
        original.put("key", "val");

        Map<String, Object> copy = YmlUtils.deepCopy(original);

        Assertions.assertFalse(copy.containsKey(null));
        Assertions.assertEquals("val", copy.get("key"));
    }
}

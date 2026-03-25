package fr.openmc.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueueTest {

    @Test
    @DisplayName("Add and get element")
    void testAddAndGet() {
        Queue<String, Integer> queue = new Queue<>(5);
        queue.add("a", 1);
        Assertions.assertEquals(1, queue.get("a"));
    }

    @Test
    @DisplayName("Remove element")
    void testRemove() {
        Queue<String, Integer> queue = new Queue<>(5);
        queue.add("a", 1);
        queue.remove("a");
        Assertions.assertNull(queue.get("a"));
    }

    @Test
    @DisplayName("Get non-existent key returns null")
    void testGet_NonExistent() {
        Queue<String, Integer> queue = new Queue<>(5);
        Assertions.assertNull(queue.get("missing"));
    }

    @Test
    @DisplayName("Eviction when exceeding size")
    void testEviction() {
        Queue<String, Integer> queue = new Queue<>(3);
        queue.add("a", 1);
        queue.add("b", 2);
        queue.add("c", 3);
        queue.add("d", 4);

        Assertions.assertNull(queue.get("a"));
        Assertions.assertEquals(2, queue.get("b"));
        Assertions.assertEquals(3, queue.get("c"));
        Assertions.assertEquals(4, queue.get("d"));
    }

    @Test
    @DisplayName("Queue respects max size")
    void testMaxSize() {
        Queue<Integer, String> queue = new Queue<>(2);
        queue.add(1, "one");
        queue.add(2, "two");
        queue.add(3, "three");

        Assertions.assertEquals(2, queue.getQueue().size());
    }

    @Test
    @DisplayName("Overwrite existing key")
    void testOverwrite() {
        Queue<String, Integer> queue = new Queue<>(5);
        queue.add("a", 1);
        queue.add("a", 99);
        Assertions.assertEquals(99, queue.get("a"));
    }

    @Test
    @DisplayName("Queue size 1 keeps only last element")
    void testSizeOne() {
        Queue<String, String> queue = new Queue<>(1);
        queue.add("first", "1");
        queue.add("second", "2");

        Assertions.assertNull(queue.get("first"));
        Assertions.assertEquals("2", queue.get("second"));
    }
}

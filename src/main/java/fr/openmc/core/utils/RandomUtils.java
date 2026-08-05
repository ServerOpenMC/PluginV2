package fr.openmc.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private static final Random random = ThreadLocalRandom.current();

    /**
     * Retourne un double aléatoire entre min et max.
     */
    public static double randomBetween(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }

    /**
     * Retourne un float aléatoire entre min et max.
     */
    public static float randomBetween(float min, float max) {
        return ThreadLocalRandom.current().nextFloat(min, max + 1);
    }

    /**
     * Retourne un long aléatoire entre min et max.
     */
    public static long randomBetween(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    /**
     * Retourne un int aléatoire entre min et max.
     */
    public static int randomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Prends une liste initial et mélange la liste en en retournant une nouvelle liste.
     * @param inital la liste initial
     * @return la liste mélangé
     */
    public static <T> List<T> generateRandomOrder(List<T> inital) {
        List<T> shuffle = new ArrayList<>(inital);
        Collections.shuffle(shuffle);
        return shuffle;
    }
}
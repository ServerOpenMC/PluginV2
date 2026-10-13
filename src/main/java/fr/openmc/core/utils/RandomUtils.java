package fr.openmc.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private RandomUtils() {}

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * Retourne un double aléatoire entre min et max.
     */
    public static double randomBetween(double min, double max) {
        if (min >= max) return min;
        return RANDOM.nextDouble(min, max);
    }


    /**
     * Retourne un float aléatoire entre min et max.
     */
    public static float randomBetween(float min, float max) {
        if (min >= max) return min;
        return RANDOM.nextFloat(min, max);
    }

    /**
     * Retourne un long aléatoire entre min et max.
     */
    public static long randomBetween(long min, long max) {
        return RANDOM.nextLong(min, max + 1);
    }

    /**
     * Retourne un int aléatoire entre min et max.
     */
    public static int randomBetween(int min, int max) {
        return RANDOM.nextInt(min, max + 1);
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
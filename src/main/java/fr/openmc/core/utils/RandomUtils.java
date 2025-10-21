package fr.openmc.core.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random();

    /**
     * Retourne un double aléatoire entre min et max.
     */
    public static double randomBetween(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    /**
     * Retourne un float aléatoire entre min et max.
     */
    public static float randomBetween(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    /**
     * Retourne un int aléatoire entre min et max.
     */
    public static int randomBetween(int min, int max) {
        return min + random.nextInt((max - min) + 1);
    }
}
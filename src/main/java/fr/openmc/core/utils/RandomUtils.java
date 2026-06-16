package fr.openmc.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
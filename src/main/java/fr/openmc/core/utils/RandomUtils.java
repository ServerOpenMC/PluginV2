package fr.openmc.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    /**
     * Retourne un double aléatoire entre min et max.
     */
    public static double randomBetween(double min, double max) {
        if (min >= max) return min;
        return ThreadLocalRandom.current().nextDouble(min, max);
    }


    /**
     * Retourne un float aléatoire entre min et max.
     */
    public static float randomBetween(float min, float max) {
        if (min >= max) return min;
        return ThreadLocalRandom.current().nextFloat(min, max);
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

    /**
     * Prends une liste initial et retourne une entrée de celle-ci.
     * @param list la liste initial
     * @return l'entrée aléatoirement choisie
     */
    public static <T> T getRandomObjectFromList(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    /**
     * Sélectionne et retourne un objet choisi aléatoirement parmi ceux fournis.
     *
     * @param objects les objets parmi lesquels effectuer la sélection
     * @return un objet choisi aléatoirement
     */
    @SafeVarargs
    public static <T> T getRandomObject(T... objects) {
        return getRandomObjectFromList(Arrays.stream(objects).toList());
    }
}
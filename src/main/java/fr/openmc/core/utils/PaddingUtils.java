package fr.openmc.core.utils;

public class PaddingUtils {

    public static String format(String str, int length) {
        int paddingTotal = length - str.length();
        int paddingLeft = paddingTotal / 2;
        int paddingRight = paddingTotal - paddingLeft;

        return " ".repeat(Math.max(0, paddingLeft)) + str + " ".repeat(Math.max(0, paddingRight));
    }
}

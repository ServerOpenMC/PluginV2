package fr.openmc.core.utils;

public class DateUtils {
    /**
     * Convert MCT to a readable duration
     * @param ticks Ticks in Minecraft
     * @return Date Format in Xj Xh Xm Xs
     */
    public static String convertTime(long ticks) {
        long millis = ticks * 50;

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;

        return String.format("%dj %dh %dm %ds", days, hours, minutes, seconds);
    }
}

package fr.openmc.core.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        public static String formatRelativeDate(LocalDateTime dateTime) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(dateTime, now);
            long minutes = duration.toMinutes();

            if (minutes < 1) {
                return "À l'instant";
            } else if (minutes < 60) {
                return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
            } else if (duration.toHours() < 24) {
                long hours = duration.toHours();
                return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");
            } else if (duration.toDays() <= 5) {
                long days = duration.toDays();
                return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Le' dd/MM/yyyy 'à' HH:mm");
                return dateTime.format(formatter);
            }
        }
}

package fr.openmc.core.utils;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class DateUtils {
    private final static DateTimeFormatter foratterWeekFormat = DateTimeFormatter.ofPattern("u-w", Locale.FRENCH);

    /**
     * Get "Previous Week Format"
     * -> 2025-34 - 1 YY-w
     * w is the week number in the year
     */
    public static String getPreviousWeekFormat() {
        LocalDate previousWeek = LocalDate.now().minusWeeks(1);

        return previousWeek.format(foratterWeekFormat);
    }

    /**
     * Get "Week Format"
     * -> 2025-34 YY-w
     * w is the week number in the year
     */
    public static String getWeekFormat() {
        LocalDate currentDate = LocalDate.now();

        return currentDate.format(foratterWeekFormat);
    }

    /**
     * Get "Next Week Format"
     * -> 2025-34 + 1 YY-w
     * w is the week number in the year
     */
    public static String getNextWeekFormat() {
        LocalDate nextWeek = LocalDate.now().plusWeeks(1);

        return nextWeek.format(foratterWeekFormat);
    }

    /**
     * Get Current day of the week
     * @return date (MONDAY, FRIDAY, SUNDAY, ...)
     */
    public static DayOfWeek getCurrentDayOfWeek() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.FRENCH);

        LocalDate currentDate = LocalDate.now();
        String currentDayString = currentDate.format(formatter);

        //conversion ex ven. => FRIDAY
        return DayOfWeek.from(formatter.parse(currentDayString));
    }

    /**
     * Convertis vos Millisecondes en un format
     * @param millis Vos millisecondes
     * @return format 3j 4h 2m 38s
     */
    public static String convertMillisToTime(long millis) {
        return formatTime(millis);
    }

    /**
     * Convertis vos Ticks en un format
     * @param ticks Vos Tick Minecraft
     * @return format 4h 2m 38s
     */
    public static String convertTime(long ticks) {
        long millis = ticks * 50;
        return formatTime(millis);
    }

    /**
     * Convertis vos secondes en un format
     * @param seconds Secondes
     * @return format 4h 2m 38s
     */
    public static String convertSecondToTime(long seconds) {
        long millis = seconds * 1000;
        return formatTime(millis);
    }

    /**
     * Convertion millis en format
     * @param millis Millisecondes
     * @return format 4h 2m 38s
     */
    private static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("j ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    /**
     * Renvoie une chaine de caractère en fonction du temps passé
     */
    public static String formatRelativeDate(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long minutes = duration.toMinutes();

        String repetMsg="Il y a ";
        if (minutes < 1) {
            return "À l'instant";
        } else if (minutes < 60) {
            return repetMsg + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (duration.toHours() < 24) {
            long hours = duration.toHours();
            return repetMsg + hours + " heure" + (hours > 1 ? "s" : "");
        } else if (duration.toDays() <= 5) {
            long days = duration.toDays();
            return repetMsg + days + " jour" + (days > 1 ? "s" : "");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Le' dd/MM/yyyy 'à' HH:mm");
            return dateTime.format(formatter);
        }
    }

    /**
     * Calcule le temps entre maintenant et lundi par exemple
     * @param day DayOfWeek
     * @return format 4h 2m 38s
     */
    public static String getTimeUntilNextDay(DayOfWeek day) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime nextDay = now.with(TemporalAdjusters.next(day)).toLocalDate().atStartOfDay();

        Duration duration = Duration.between(now, nextDay);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        return String.format("%dd %dh %dm", days, hours, minutes);
    }

    public static long getSecondsUntilDayOfWeekMidnight(DayOfWeek dayOfWeek) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDayOfWeekMidnight = now.with(TemporalAdjusters.nextOrSame(dayOfWeek))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        if (!now.isBefore(nextDayOfWeekMidnight)) {
            nextDayOfWeekMidnight = nextDayOfWeekMidnight.plusWeeks(1);
        }

        return ChronoUnit.SECONDS.between(now, nextDayOfWeekMidnight);
    }
}

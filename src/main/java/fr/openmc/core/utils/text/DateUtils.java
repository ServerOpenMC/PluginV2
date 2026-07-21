package fr.openmc.core.utils.text;

import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class DateUtils {

    private final static DateTimeFormatter foratterWeekFormat = DateTimeFormatter.ofPattern("u-w", Locale.FRENCH);

    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now(ZoneId.of("Europe/Paris"));
    }
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

    public static boolean isBefore(String weekStr1, String weekStr2) {
        String[] weekParts1 = weekStr1.split("-");
        String[] weekParts2 = weekStr2.split("-");

        int year1 = Integer.parseInt(weekParts1[0]);
        int year2 = Integer.parseInt(weekParts2[0]);

        int week1 = Integer.parseInt(weekParts1[1]);
        int week2 = Integer.parseInt(weekParts2[1]);

        if (year1 < year2) {
            return true;
        }

        return year1 == year2 && week1 <= week2;
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
    public static Component formatRelativeDate(LocalDateTime dateTime) {
        LocalDateTime now = DateUtils.getLocalDateTime();
        Duration duration = Duration.between(dateTime, now);
        long minutes = duration.toMinutes();

        if (minutes < 1) {
            return TranslationManager.translation("core.date.relative.just_now");
        } else if (minutes < 60) {
            return TranslationManager.translation(
                    minutes > 1 ? "core.date.relative.minutes" : "core.date.relative.minute",
                    Component.text(minutes)
            );
        } else if (duration.toHours() < 24) {
            long hours = duration.toHours();
            return TranslationManager.translation(
                    hours > 1 ? "core.date.relative.hours" : "core.date.relative.hour",
                    Component.text(hours)
            );
        } else if (duration.toDays() <= 5) {
            long days = duration.toDays();
            return TranslationManager.translation(
                    days > 1 ? "core.date.relative.days" : "core.date.relative.day",
                    Component.text(days)
            );
        } else {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return TranslationManager.translation(
                    "core.date.relative.absolute",
                    Component.text(dateTime.format(dateFormatter)),
                    Component.text(dateTime.format(timeFormatter))
            );
        }
    }

    /**
     * Renvoie une chaine de caractère (ex dimanche 7 juin)
     */
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRANCE)
                + " " + dateTime.getDayOfMonth() + " "
                + dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE);
    }

    /**
     * Renvoie une chaine de caractère (ex 0h12)
     */
    public static String formatHourMinute(int hours, int minutes) {
        return hours + "h" + String.format("%02d", minutes);
    }

    /**
     * Calcule le temps entre maintenant et lundi par exemple
     * @param day DayOfWeek
     * @return format 4h 2m 38s
     */
    public static String getTimeUntilNextDay(DayOfWeek day) {
        LocalDateTime now = DateUtils.getLocalDateTime();

        LocalDateTime nextDay = now.with(TemporalAdjusters.next(day)).toLocalDate().atStartOfDay();

        Duration duration = Duration.between(now, nextDay);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        return String.format("%dd %dh %dm", days, hours, minutes);
    }

    public static long getSecondsUntilDayOfWeekTime(DayOfWeek dayOfWeek, int hour, int minute, int second) {
        LocalDateTime now = DateUtils.getLocalDateTime();
        LocalDateTime nextDayOfWeekMidnight = now.with(TemporalAdjusters.nextOrSame(dayOfWeek))
                .withHour(hour).withMinute(minute).withSecond(second).withNano(0);

        if (!now.isBefore(nextDayOfWeekMidnight)) {
            nextDayOfWeekMidnight = nextDayOfWeekMidnight.plusWeeks(1);
        }

        return ChronoUnit.SECONDS.between(now, nextDayOfWeekMidnight);
    }

    public static long getDelayBetweenNow(LocalDateTime time) {
        LocalDateTime now = getLocalDateTime();

        return ChronoUnit.SECONDS.between(now, time);
    }
}

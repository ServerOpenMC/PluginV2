package fr.openmc.core.features.economy.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class EconomyUtils {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>(Map.of(
            1_000L, "k",
            1_000_000L, "M",
            1_000_000_000L, "B",
            1_000_000_000_000L, "T",
            1_000_000_000_000_000L, "Qa",
            1_000_000_000_000_000_000L, "Qi"));

    public static String getFormattedNumber(double number, String currencyIcon) {
        Currency currency = Currency.getInstance(Locale.FRANCE);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        format.setCurrency(currency);
        BigDecimal bd = new BigDecimal(number);

        return format.format(bd).replace(NumberFormat.getCurrencyInstance(Locale.FRANCE).getCurrency().getSymbol(),
                currencyIcon);
    }

    public static String getFormattedSimplifiedNumber(double balance) {
        if (balance == 0) return "0";

        Map.Entry<Long, String> entry = SUFFIXES.floorEntry((long) balance);
        if (entry == null) return decimalFormat.format(balance);

        long divideBy = entry.getKey();
        String suffix = entry.getValue();

        double truncated = balance / divideBy;
        String formatted = decimalFormat.format(truncated);

        return formatted + suffix;
    }
}

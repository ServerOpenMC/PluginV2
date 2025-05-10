package fr.openmc.core.features.economy;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.economy.commands.Baltop;
import fr.openmc.core.features.economy.commands.History;
import fr.openmc.core.features.economy.commands.Money;
import fr.openmc.core.features.economy.commands.Pay;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class EconomyManager {
    @Getter private static Map<UUID, Double> balances;
    @Getter static EconomyManager instance;

    private final DecimalFormat decimalFormat;
    private final NavigableMap<Long, String> suffixes;

    public EconomyManager() {
        balances = EconomyData.loadBalances();
        instance = this;

        decimalFormat = new DecimalFormat("#.##");
        suffixes = new TreeMap<>();
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "Q");
        suffixes.put(1_000_000_000_000_000_000L, "Qi");

        CommandsManager.getHandler().register(
                new Pay(),
                new Baltop(),
                new History(),
                new Money()
        );
    }
    
    public static double getBalance(UUID player) {
        return balances.getOrDefault(player, 0.0);
    }
    
    public static void addBalance(UUID player, double amount) {
        double balance = getBalance(player);
        balance += amount;
        balances.put(player, balance);
        saveBalances(player);
    }
    
    public static boolean withdrawBalance(UUID player, double amount) {
        double balance = getBalance(player);
        if(balance >= amount) {
            balance -= amount;
            balances.put(player, balance);
            saveBalances(player);
            return true;
        }
        return false;
    }

    public void setBalance(UUID player, double amount) {
        balances.put(player, amount);
        saveBalances(player);
    }
    
    public static void saveBalances(UUID player) {
        EconomyData.saveBalances(player, getBalance(player));
    }

    public String getMiniBalance(UUID player) {
        double balance = getBalance(player);

        return getFormattedSimplifiedNumber(balance);
    }

    public String getFormattedBalance(UUID player) {
        String balance = String.valueOf(getBalance(player));
        Currency currency = Currency.getInstance(Locale.FRANCE);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        format.setCurrency(currency);
        BigDecimal bd = new BigDecimal(balance);
        return format.format(bd).replace(NumberFormat.getCurrencyInstance(Locale.FRANCE).getCurrency().getSymbol(), getEconomyIcon());
    }

    public String getFormattedNumber(double number) {
        Currency currency = Currency.getInstance(Locale.FRANCE);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        format.setCurrency(currency);
        BigDecimal bd = new BigDecimal(number);
        return  format.format(bd).replace(NumberFormat.getCurrencyInstance(Locale.FRANCE).getCurrency().getSymbol(), getEconomyIcon());
    }

    public static String getFormattedSimplifiedNumber(double balance) {
        if (balance == 0) {
            return "0";
        }
    
        Map.Entry<Long, String> entry = instance.suffixes.floorEntry((long) balance);
        if (entry == null) {
            return instance.decimalFormat.format(balance);
        }
    
        long divideBy = entry.getKey();
        String suffix = entry.getValue();
    
        double truncated = balance / divideBy;
        String formatted = instance.decimalFormat.format(truncated);
    
        return formatted + suffix;
    }

    public static String getEconomyIcon() {
        if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return "§f" + PlaceholderAPI.setPlaceholders(null, "%img_aywenito%");
        }
        return "Ⓐ";
    }
}

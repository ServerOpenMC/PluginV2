package fr.openmc.core.features.economy;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.economy.commands.Baltop;
import fr.openmc.core.features.economy.commands.History;
import fr.openmc.core.features.economy.commands.Money;
import fr.openmc.core.features.economy.commands.Pay;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class EconomyManager {
    @Getter private static Map<UUID, Double> balances;
    @Getter static EconomyManager instance;

    public EconomyManager() {
        balances = EconomyData.loadBalances();
        instance = this;
        CommandsManager.getHandler().register(
                new Pay(),
                new Baltop(),
                new History(),
                new Money()
        );
    }

    public double getBalance(UUID player) {
        return balances.getOrDefault(player, 0.0);
    }

    public void addBalance(UUID player, double amount) {
        double balance = getBalance(player);
        balance += amount;
        balances.put(player, balance);
        saveBalances(player);
    }

    public boolean withdrawBalance(UUID player, double amount) {
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

    public void saveBalances(UUID player) {
        EconomyData.saveBalances(player, getBalance(player));
    }

    public String getMiniBalance(UUID player) {
        double balance = getBalance(player);

        DecimalFormat df = new DecimalFormat("#.##");
        return balance >= 1000000000 ? df.format(balance / 1000000000) + "B" :
               balance >= 1000000 ? df.format(balance / 1000000) + "M" :
               balance >= 1000 ? df.format(balance / 1000) + "k" :
               df.format(balance);
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

    public static String getEconomyIcon() {
        if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return ChatColor.RESET +  PlaceholderAPI.setPlaceholders(null, "%img_aywenito%");
        }
        return "Ⓐ";
    }

}

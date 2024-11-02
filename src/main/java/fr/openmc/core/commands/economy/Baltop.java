package fr.openmc.core.commands.economy;

import fr.openmc.core.features.utils.economy.EconomyManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;

public class Baltop {

    @Command("baltop")
    @Description("Permet de voir le top des joueurs les plus riches")
    @CommandPermission("omc.commands.baltop")
    public void baltop(Player player) {
        List<PlayerBalance> balances = getBalances();

        balances.sort((a, b) -> a.balance.intValue() - b.balance.intValue());

        balances = balances.reversed();

        if(balances.size() > 10) {
            balances = balances.subList(0, 10);
        }

        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.DARK_GREEN + "Liste des 10 joueurs les plus riches");

        int index = 1;
        for(PlayerBalance balance : balances) {
            lines.add(MessageFormat.format("{0}. {1}: {2}", getColor(index) + index, ChatColor.GRAY + Bukkit.getOfflinePlayer(balance.playerId).getName(), ChatColor.GREEN + balance.getFormattedBalance()));            index++;
        }

        player.sendMessage(String.join("\n", lines));
    }

    public static String getColor(int index) {
        return (switch (index) {
            case 1 -> ChatColor.GOLD;
            case 2 -> ChatColor.of("#d7d7d7");
            case 3 -> ChatColor.of("#945604");
            default -> ChatColor.WHITE;
        }).toString();
    }


    public static List<PlayerBalance> getBalances() {
        List<PlayerBalance> balances = new ArrayList<>();
        EconomyManager.getBalances().forEach((playerId, balance) -> {
            balances.add(new PlayerBalance(playerId, balance));
        });
        return balances;
    }

    public static class PlayerBalance {
        public UUID playerId;
        public Double balance;

        public PlayerBalance(UUID playerId, Double balance) {
            this.playerId = playerId;
            this.balance = balance;
        }

        public String getFormattedBalance() {
            String balance = String.valueOf(this.balance);
            Currency currency = Currency.getInstance("EUR");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
            formatter.setCurrency(currency);
            BigDecimal bd = new BigDecimal(balance);
            return formatter.format(bd);
        }
    }
}

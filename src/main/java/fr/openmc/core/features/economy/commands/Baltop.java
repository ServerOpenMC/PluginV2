package fr.openmc.core.features.economy.commands;

import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

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

        TextComponent.Builder builder = Component.text()
                .content("§6§lTop 10 des joueurs les plus riches\n\n")
                .color(TextColor.color(0xffd700));

        int index = 1;
        for(PlayerBalance balance : balances) {
            builder.append(Component.text(index + ". ", getColor(index)))
                    .append(Component.text(Bukkit.getOfflinePlayer(balance.playerId).getName() + ": ", NamedTextColor.GRAY))
                    .append(Component.text(EconomyManager.getInstance().getFormattedNumber(balance.balance) + "\n", NamedTextColor.GREEN));
            index++;
        }

        player.sendMessage(builder.build());
    }

    public static TextColor getColor(int index) {
        return switch (index) {
            case 1 -> TextColor.color(0xffd700);
            case 2 -> TextColor.color(0xd7d7d7);
            case 3 -> TextColor.color(0x945604);
            default -> NamedTextColor.WHITE;
        };
    }


    public static List<PlayerBalance> getBalances() {
        List<PlayerBalance> balances = new ArrayList<>();
        EconomyManager.getBalances().forEach((playerId, balance) -> {
            balances.add(new PlayerBalance(playerId, balance));
        });
        return balances;
    }

    public static class PlayerBalance {
        public final UUID playerId;
        public final Double balance;

        public PlayerBalance(UUID playerId, Double balance) {
            this.playerId = playerId;
            this.balance = balance;
        }
    }
}

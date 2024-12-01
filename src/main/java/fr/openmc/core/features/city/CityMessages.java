package fr.openmc.core.features.city;

import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CityMessages {
    private static void sendLine(Audience audience, String title, String info) {
        audience.sendMessage(Component.text(title+": ").append(
                Component.text(info)
                        .color(NamedTextColor.LIGHT_PURPLE)
        ));
    }

    public static void sendInfo(CommandSender sender, City city) {
        String cityName = city.getName();
        String mayorName = Bukkit.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName();
        int citizens = city.getMembers().size();
        int area = city.getChunks().size();

        sender.sendMessage(
                Component.text("--- ").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false).append(
                Component.text(cityName).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, true).append(
                Component.text(" ---").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false)
        )));

        sendLine(sender, "Maire", mayorName);
        sendLine(sender, "Habitants", String.valueOf(citizens));
        sendLine(sender, "Superficie", String.valueOf(area));

        if (sender instanceof Player player) {
            if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE))) return;
            sendLine(sender, "Banque", city.getBalance()+ EconomyManager.getEconomyIcon());
        } else {
            sendLine(sender, "Banque", city.getBalance()+ EconomyManager.getEconomyIcon());
        }
    }
}

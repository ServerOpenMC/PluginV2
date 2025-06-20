package fr.openmc.core.features.city;

import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CityMessages {
    private static void sendLine(Audience audience, String title, String info) {
        audience.sendMessage(Component.text(title+": ").append(
                Component.text(info)
                        .color(NamedTextColor.LIGHT_PURPLE)
        ));
    }

    public static void sendInfo(CommandSender sender, City city) {
        String mascotLife = "dead";
        String cityName = city.getName();
        String mayorName = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission(CPermission.OWNER)).getName();

        int citizens = city.getMembers().size();
        int area = city.getChunks().size();
        int power = city.getPowerPoints();

        CityType type = city.getType();
        String typeString;
        if (type == CityType.WAR) {
            typeString = "Guerre";
        } else if (type == CityType.PEACE) {
            typeString = "Paix";
        } else {
            typeString = "Inconnu";
        }
        Mascot mascot = city.getMascot();
        if (mascot!=null){
            LivingEntity mob = (LivingEntity) mascot.getEntity();
            if (mascot.isAlive()) {
                mascotLife = String.valueOf(mob.getHealth());
            }
        }

        sender.sendMessage(
                Component.text("--- ").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false).append(
                Component.text(cityName).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, true).append(
                Component.text(" ---").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false)
        )));

        sendLine(sender, "Maire", mayorName);
        sendLine(sender, "Habitants", String.valueOf(citizens));
        sendLine(sender, "Superficie", String.valueOf(area));
        if (type == CityType.WAR) {
            sendLine(sender, "Puissance", String.valueOf(power));
        }
        sendLine(sender, "Vie de la Mascotte", mascotLife);
        sendLine(sender, "Type", typeString);

        String money = EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon();
        if (sender instanceof Player player) {
            if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE))) return;
            sendLine(sender, "Banque", money);
        } else {
            sendLine(sender, "Banque", money);
        }

        if (city.getFreeClaims() > 0)
            sendLine(sender, "Claim gratuit", String.valueOf(city.getFreeClaims()));
    }
}

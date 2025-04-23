package fr.openmc.core.features.city;

import fr.openmc.core.features.city.mascots.Mascot;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
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
        String mayorName = Bukkit.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName();

        int citizens = city.getMembers().size();
        int area = city.getChunks().size();
        int power = CityManager.getCityPowerPoints(city.getUUID());

        String type = CityManager.getCityType(city.getUUID());
        Mascot mascot = MascotUtils.getMascotOfCity(city.getUUID());
        if (mascot!=null){
            LivingEntity mob = MascotUtils.loadMascot(mascot);
            if (MascotUtils.getMascotState(city.getUUID())){
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
        if (type!=null && type.equals("war")){
            sendLine(sender, "Puissance", String.valueOf(power));
        }
        sendLine(sender, "Vie de la Mascotte", mascotLife);
        sendLine(sender, "Type", type);

        String money = EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon();
        if (sender instanceof Player player) {
            if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE))) return;
            sendLine(sender, "Banque", money);
        } else {
            sendLine(sender, "Banque", money);
        }
        if (CityManager.freeClaim.containsKey(city.getUUID())){
            sendLine(sender, "Claim gratuit", String.valueOf(CityManager.freeClaim.get(city.getUUID())));
        }
    }
}

package fr.openmc.core.features.city;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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

    public static void sendInfo(CommandSender sender, String cityUUID) {
        String cityName = CityManager.getCityName(cityUUID);
        String mayorName = Bukkit.getOfflinePlayer(CityPermissions.getPlayerWith(cityUUID, CPermission.OWNER)).getName();
        int citizens = CityManager.getMembers(cityUUID).size();
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
        ProtectedRegion region = regionManager.getRegion("city_"+cityUUID);
        int area = (int) Math.ceil(CityUtils.getPolygonalRegionArea(region)/256);

        if (cityName == null) {
            cityName = "Inconnu";
        }

        sender.sendMessage(
                Component.text("--- ").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false).append(
                Component.text(cityName).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, true).append(
                Component.text(" ---").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false)
        )));

        sendLine(sender, "Maire", mayorName);
        sendLine(sender, "Habitants", String.valueOf(citizens));
        sendLine(sender, "Superficie", String.valueOf(area));

        if (sender instanceof Player player) {
            if (!(CityPermissions.hasPermission(cityUUID, player.getUniqueId(), CPermission.MONEY_BALANCE))) return;
            sendLine(sender, "Banque", CityManager.getBalance(cityUUID)+ EconomyManager.getEconomyIcon());
        } else {
            sendLine(sender, "Banque", CityManager.getBalance(cityUUID)+ EconomyManager.getEconomyIcon());
        }
    }
}

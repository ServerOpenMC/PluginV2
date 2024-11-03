package fr.openmc.core.features.city;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.openmc.core.features.utils.economy.EconomyManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

public class CityMessages {
    private static void sendLine(Audience audience, String title, String info) {
        audience.sendMessage(Component.text(title+": ").append(
                Component.text(info)
                        .color(NamedTextColor.LIGHT_PURPLE)
        ));
    }

    public static void sendInfo(Audience audience, String cityUUID) {
        String cityName = CityManager.getCityName(cityUUID);
        String mayorName = Bukkit.getOfflinePlayer(CityManager.getOwnerUUID(cityUUID)).getName();
        int citizens = CityManager.getMembers(cityUUID).size();
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
        ProtectedRegion region = regionManager.getRegion("city_"+cityUUID);
        int area = (int) Math.ceil(CityUtils.getPolygonalRegionArea(region)/256);

        if (cityName == null) {
            cityName = "Inconnu";
        }

        audience.sendMessage(
                Component.text("--- ").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false).append(
                Component.text(cityName).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, true).append(
                Component.text(" ---").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false)
        )));

        sendLine(audience, "Maire", mayorName);
        sendLine(audience, "Habitants", String.valueOf(citizens));
        sendLine(audience, "Banque", CityManager.getBalance(cityUUID)+ EconomyManager.getEconomyIcon());
        sendLine(audience, "Superficie", String.valueOf(area));
    }
}

package fr.openmc.core.features.city;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

import java.util.List;

public class CityMessages {
    private static void sendLine(Audience audience, String title, String info) {
        audience.sendMessage(Component.text(title+") ").append(
                Component.text(info)
                        .color(NamedTextColor.LIGHT_PURPLE)
        ));
    }

    public static double getPolygonalRegionArea(ProtectedRegion region) {
        ProtectedPolygonalRegion polygonRegion = (ProtectedPolygonalRegion) region;
        List<BlockVector2> points = polygonRegion.getPoints();

        return calculatePolygonArea(points);
    }

    private static double calculatePolygonArea(List<BlockVector2> points) {
        double area = 0.0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            BlockVector2 p1 = points.get(i);
            BlockVector2 p2 = points.get((i + 1) % n); // Wrap around to the first point

            area += p1.x() * p2.z() - p2.x() * p1.z();
        }

        area = Math.abs(area) / 2.0;
        return area;
    }

    public static void sendInfo(Audience audience, String cityUUID) {
        String cityName = CityManager.getCityName(cityUUID);
        String mayorName = Bukkit.getOfflinePlayer(CityManager.getOwnerUUID(cityUUID)).getName();
        int citizens = CityManager.getMembers(cityUUID).size();
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
        ProtectedRegion region = regionManager.getRegion("city_"+cityUUID);
        int area = (int) Math.ceil(getPolygonalRegionArea(region)/256);

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
        sendLine(audience, "Banque", cityUUID);
        sendLine(audience, "Superficie", String.valueOf(area));
    }
}

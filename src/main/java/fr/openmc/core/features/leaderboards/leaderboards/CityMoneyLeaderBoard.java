package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.features.leaderboards.LeaderBoardManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.entities.TextDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.List;

public class CityMoneyLeaderBoard extends LeaderBoard {

    public CityMoneyLeaderBoard() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());
        float scale = (float) config.getDouble("scale");
        super("cities-money", config.getLocation("city-money-location"), null, 15);
        if (this.location != null)
            this.display = new TextDisplay(createComponent(), this.location, new Vector3f(scale));
        else
            OMCLogger.warn("city-money-location is null");
    }



    @Override
    public Component createComponent(){
        List<City> cities = CityManager.getCities().stream()
                .sorted((city1, city2) -> Double.compare(city2.getBalance(), city1.getBalance()))
                .limit(10)
                .toList();
        if (cities.isEmpty())
            return TranslationManager.translation("feature.leaderboards.empty.cities")
                    .color(NamedTextColor.RED);
        Component text = TranslationManager.translation("feature.leaderboards.header.city_money")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (int i = 0; i < cities.size(); i++){
            City city = cities.get(i);
            Component rank = Component.text("#" + (i+1)).color(LeaderBoardManager.getRankColor(i+1));
            Component cityBalance = Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon())
                    .color(NamedTextColor.WHITE);
            text = text.append(Component.text("\n").append(TranslationManager.translation(
                            "feature.leaderboards.line.city_money",
                                rank,
                                Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE),
                                cityBalance)));
        }
        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD)));
    }

    @Override
    public void setLocation(Location location) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());
        config.set("city-money-location", location);
        config.save(LeaderBoardManager.getLeaderBoardFile());
        this.display.setLocation(location);
    }
}

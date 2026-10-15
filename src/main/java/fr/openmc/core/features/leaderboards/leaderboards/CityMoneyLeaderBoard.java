package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

public class CityMoneyLeaderBoard extends LeaderBoard {

    @Override
    public double getUpdateDelay() {
        return 15;
    }

    @Override
    public String getId() {
        return "cities-money";
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
            Component rank = Component.text("#" + (i+1)).color(ColorUtils.getRankColor(i+1));
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
}

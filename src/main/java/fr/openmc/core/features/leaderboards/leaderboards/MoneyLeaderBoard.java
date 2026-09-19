package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.features.leaderboards.LeaderBoardManager;
import fr.openmc.core.utils.cache.CachePlayerName;
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
import java.util.*;

public class MoneyLeaderBoard extends LeaderBoard {

    public MoneyLeaderBoard(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());
        float scale = (float) config.getDouble("scale");
        super("money", config.getLocation("money-location"), null, 15);
        if (this.location != null)
            this.display = new TextDisplay(createComponent(), this.location, new Vector3f(scale));
        else
            OMCLogger.warn("money-location is null");
    }

    @Override
    public Component createComponent() {
        Map<UUID, Double> tmp_balances = new HashMap<>();
        EconomyManager.getBalances().forEach((player, balance) -> tmp_balances.put(player, balance.getBalance()));
        BankManager.getBanks().forEach((uuid, bank) -> tmp_balances.merge(uuid, bank.getBalance(), Double::sum));

        if (tmp_balances.isEmpty())
            return TranslationManager.translation("feature.leaderboards.empty.players")
                    .color(NamedTextColor.RED);

        List<Map.Entry<UUID, Double>> balances = tmp_balances.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(10)
                .toList();

        Component text = TranslationManager.translation("feature.leaderboards.header.money")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (int i = 0;i < balances.size(); i++){
            Map.Entry<UUID, Double> balance = balances.get(i);
            Component rank = Component.text("#" + (i+1)).color(LeaderBoardManager.getRankColor(i+1));

            text = text.append(Component.text("\n")
                    .append(TranslationManager.translation(
                            "feature.leaderboards.line.money",
                            rank,
                            Component.text(CachePlayerName.getName(balance.getKey())).color(NamedTextColor.LIGHT_PURPLE),
                            Component.text(EconomyManager.getFormattedSimplifiedNumber(balance.getValue()) + " " + EconomyManager.getEconomyIcon())
                                    .color(NamedTextColor.WHITE)
                    )));
        }

        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD)));
    }

    @Override
    public void setLocation(Location location) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());
        config.set("money-location", location);
        config.save(LeaderBoardManager.getLeaderBoardFile());
        this.display.setLocation(location);
    }
}

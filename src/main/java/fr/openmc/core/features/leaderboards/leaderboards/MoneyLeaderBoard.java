package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.utils.EconomyUtils;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.utils.cache.CachePlayerName;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoneyLeaderBoard extends LeaderBoard {
    private final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();
    private final BankManager bankManager = OMCRegistry.FEATURES.BANK.get();

    @Override
    public double getUpdateDelay() {
        return 15;
    }

    @Override
    public String getId() {
        return "money";
    }

    @Override
    public Component createComponent() {
        Map<UUID, Double> tempBalances = new HashMap<>();
        economyManager.getBalances().forEach((player, balance) -> tempBalances.put(player, balance.getBalance()));
        bankManager.getBanks().forEach((uuid, bank) -> tempBalances.merge(uuid, bank.getBalance(), Double::sum));

        if (tempBalances.isEmpty())
            return TranslationManager.translation("feature.leaderboards.empty.players")
                    .color(NamedTextColor.RED);

        List<Map.Entry<UUID, Double>> balances = tempBalances.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(10)
                .toList();

        Component text = TranslationManager.translation("feature.leaderboards.header.money")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (int i = 0;i < balances.size(); i++){
            Map.Entry<UUID, Double> balance = balances.get(i);
            Component rank = Component.text("#" + (i+1)).color(ColorUtils.getRankColor(i+1));

            text = text.append(Component.text("\n")
                    .append(TranslationManager.translation(
                            "feature.leaderboards.line.money",
                            rank,
                            CachePlayerName.name(balance.getKey()).color(NamedTextColor.LIGHT_PURPLE),
                            Component.text(EconomyUtils.getFormattedSimplifiedNumber(balance.getValue())
                                            + " " + economyManager.getEconomyIcon())
                                    .color(NamedTextColor.WHITE)
                    )));
        }

        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD)));
    }
}

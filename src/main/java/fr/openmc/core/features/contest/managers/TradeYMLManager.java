package fr.openmc.core.features.contest.managers;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.contest.models.Contest;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TradeYMLManager {
    @Getter
    private static File contestFile;
    @Getter
    private static YamlConfiguration contestConfig;

    public TradeYMLManager() {
        contestFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "contest.yml");
        loadContestConfig();
    }

    /**
     * Charge le contest.yml
     */
    private static void loadContestConfig() {
        if (!contestFile.exists()) {
            contestFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/contest.yml", false);
        }

        contestConfig = YamlConfiguration.loadConfiguration(contestFile);
    }

    /**
     * Sauvegarde du contest.yml
     */
    public static void saveContestConfig() {
        try {
            contestConfig.save(contestFile);
        } catch (IOException e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Failed to save contest configuration file: {}", e.getMessage(), e);
        }
    }

    /**
     * Retourne une Liste avec les trades selectionnés donc le bool pour savoir si le trade est déjà
     * choisis ou pas
     */
    public static List<Map<String, Object>> getTradeSelected(boolean bool) {
        List<Map<?, ?>> contestTrades = contestConfig.getMapList("contestTrades");

        List<Map<String, Object>> filteredTrades = contestTrades.stream()
                .filter(trade -> (boolean) trade.get("selected") == bool)
                .map(trade -> (Map<String, Object>) trade)
                .collect(Collectors.toList());

        Collections.shuffle(filteredTrades);

        return filteredTrades.stream().limit(12).collect(Collectors.toList());
    }


    /**
     * Change le boolean, si il est true il sera false
     */
    public static void updateColumnBooleanFromRandomTrades(Boolean bool, String ress) {
        List<Map<String, Object>> contestTrades = (List<Map<String, Object>>) contestConfig.get("contestTrades");

        for (Map<String, Object> trade : contestTrades) {
            if (trade.get("ress").equals(ress)) {
                trade.put("selected", bool);
            }
        }
        saveContestConfig();
    }

    /**
     * Retourne une Liste contenant les ressources (ex NETHERITE_BLOCK)
     */
    public static List<String> getRessListFromConfig() {
        FileConfiguration config = OMCPlugin.getInstance().getConfig();
        List<Map<?, ?>> trades = config.getMapList("contestTrades");
        List<String> ressList = new ArrayList<>();

        for (Map<?, ?> tradeEntry : trades) {
            if (tradeEntry.containsKey("ress")) {
                ressList.add(tradeEntry.get("ress").toString());
            }
        }
        return ressList;
    }


    private static void updateSelected(String camp) {
        List<Map<?, ?>> contestList = contestConfig.getMapList("contestList");
        List<Map<String, Object>> updatedContestList = new ArrayList<>();

        for (Map<?, ?> contest : contestList) {
            Map<String, Object> fusionContestList = new HashMap<>();

            for (Map.Entry<?, ?> entry : contest.entrySet()) {
                if (entry.getKey() instanceof String) {
                    fusionContestList.put((String) entry.getKey(), entry.getValue());
                }
            }

            if (fusionContestList.get("camp1").equals(camp)) {
                int selected = (int) fusionContestList.get("selected");
                fusionContestList.put("selected", selected + 1);
            }

            updatedContestList.add(fusionContestList);
        }
        contestConfig.set("contestList", updatedContestList);
        saveContestConfig();
    }

    /**
     * On ajoute 1 au dernier Contest pour éviter qu'il revienne (il revient seulement si tout les contests sont passés
     */
    public static void addOneToLastContest(String camps) {
        List<Map<?, ?>> contestList = contestConfig.getMapList("contestList");

        for (Map<?, ?> contest : contestList) {
            if (contest.get("camp1").equals(camps)) {
                updateSelected(camps);
            }
        }
    }

    /**
     * Pioche un Conests en fonction de son nombre de selection
     */
    public static void selectRandomlyContest() {
        List<Map<?, ?>> contestList = contestConfig.getMapList("contestList");
        List<Map<String, Object>> orderedContestList = new ArrayList<>();

        for (Map<?, ?> contest : contestList) {
            Map<String, Object> fusionContest = new HashMap<>();
            for (Map.Entry<?, ?> entry : contest.entrySet()) {
                if (entry.getKey() instanceof String) {
                    fusionContest.put((String) entry.getKey(), entry.getValue());
                }
            }
            orderedContestList.add(fusionContest);
        }

        int minSelected = orderedContestList.stream()
                .mapToInt(c -> (int) c.get("selected"))
                .min()
                .orElse(0);

        List<Map<String, Object>> leastSelectedContests = orderedContestList.stream()
                .filter(c -> (int) c.get("selected") == minSelected)
                .toList();

        Random random = new Random();
        Map<String, Object> selectedContest = leastSelectedContests.get(random.nextInt(leastSelectedContests.size()));

        ContestManager.data = new Contest((String) selectedContest.get("camp1"), (String) selectedContest.get("camp2"), (String) selectedContest.get("color1"), (String) selectedContest.get("color2"), 1, "ven.", 0, 0);
    }
}

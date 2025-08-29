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

/**
 * Gestionnaire du fichier YML de contest.
 * Permet le chargement, la sauvegarde et la modification de la configuration des trades et contests.
 */
public class TradeYMLManager {
    /**
     * Fichier de configuration contest.yml.
     */
    @Getter
    private static File contestFile;

    /**
     * Objet de configuration Yaml associé au fichier contest.yml.
     */
    @Getter
    private static YamlConfiguration contestConfig;

    /**
     * Constructeur de TradeYMLManager.
     * Initialise le fichier contest.yml et charge sa configuration.
     */
    public TradeYMLManager() {
        contestFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "contest.yml");
        loadContestConfig();
    }

    /**
     * Charge la configuration du fichier contest.yml.
     * Si le fichier n'existe pas, il est créé à partir de la ressource par défaut.
     */
    private static void loadContestConfig() {
        if (!contestFile.exists()) {
            contestFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/contest.yml", false);
        }
        contestConfig = YamlConfiguration.loadConfiguration(contestFile);
    }

    /**
     * Sauvegarde la configuration actuelle dans contest.yml.
     * En cas d'erreur lors de la sauvegarde, un avertissement est loggé.
     */
    public static void saveContestConfig() {
        try {
            contestConfig.save(contestFile);
        } catch (IOException e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Failed to save contest configuration file: {}", e.getMessage(), e);
        }
    }

    /**
     * Retourne une liste de trades sélectionnés en fonction du booléen fourni.
     *
     * @param bool vrai pour récupérer les trades sélectionnés, faux sinon.
     * @return une liste de trades sous forme de Map avec les clés et valeurs correspondantes.
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
     * Met à jour le booléen "selected" d'un trade dans la configuration.
     *
     * @param bool la nouvelle valeur du booléen.
     * @param ress la ressource associée au trade à mettre à jour.
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
     * Extrait la liste des ressources à partir de la configuration.
     *
     * @return une liste de chaînes contenant les ressources (ex : NETHERITE_BLOCK).
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

    /**
     * Met à jour le nombre de sélection pour un contest donné.
     * Incrémente la valeur de "selected" si le camp correspond.
     *
     * @param camp le nom du camp à mettre à jour.
     */
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
     * Ajoute 1 au compte de sélection du dernier contest associé au camp fourni.
     * Ceci permet d'éviter que ce contest ne soit repioche.
     *
     * @param camps le nom du camp pour lequel la mise à jour est effectuée.
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
     * Sélectionne aléatoirement un contest basé sur le nombre de sélection le plus faible.
     * Le contest sélectionné est assigné à {@code ContestManager.data}.
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

        ContestManager.data = new Contest(
                (String) selectedContest.get("camp1"),
                (String) selectedContest.get("camp2"),
                (String) selectedContest.get("color1"),
                (String) selectedContest.get("color2"),
                1,
                "ven.",
                0,
                0
        );
    }
}

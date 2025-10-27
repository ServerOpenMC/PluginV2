package fr.openmc.core.features.leaderboards;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.models.EconomyPlayer;
import fr.openmc.core.features.events.halloween.managers.HalloweenManager;
import fr.openmc.core.features.events.halloween.models.HalloweenData;
import fr.openmc.core.features.leaderboards.commands.LeaderboardCommands;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.entities.TextDisplay;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.*;

public class LeaderboardManager {
    @Getter
    private static final Map<Integer, Map.Entry<String, ContributorStats>> githubContributorsMap = new TreeMap<>();
    @Getter
    private static final Map<Integer, Map.Entry<String, String>> playerMoneyMap = new TreeMap<>();
    @Getter
    private static final Map<Integer, Map.Entry<String, String>> villeMoneyMap = new TreeMap<>();
    @Getter
    private static final Map<Integer, Map.Entry<String, String>> playTimeMap = new TreeMap<>();
    @Getter
    private static final Map<Integer, Map.Entry<String, String>> pumpkinCountMap = new TreeMap<>();
    private static final File leaderBoardFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "leaderboards.yml");
    @Getter
    private static Location contributorsHologramLocation;
    @Getter
    private static Location moneyHologramLocation;
    @Getter
    private static Location villeMoneyHologramLocation;
    @Getter
    private static Location playTimeHologramLocation;
    @Getter
    private static Location pumpkinCountHologramLocation;
    private static BukkitTask taskTimer;
    private static float scale;
    private static TextDisplay contributorsHologram;
    private static TextDisplay moneyHologram;
    private static TextDisplay villeMoneyHologram;
    private static TextDisplay playTimeHologram;
    private static TextDisplay pumpkinCountHologram;

    public static void init() {
        loadLeaderBoardConfig();
        CommandsManager.getHandler().register(new LeaderboardCommands());
        enable();
    }

    /**
     * Creates the leaderboard text for GitHub contributors to be sent in chat or displayed as a hologram.
     *
     * @return A Component representing the GitHub contributors leaderboard.
     */
    public static Component createContributorsTextLeaderboard() {
        var contributorsMap = new TreeMap<>(LeaderboardManager.getGithubContributorsMap());
        if (contributorsMap.isEmpty()) {
            return Component.text("Aucun contributeur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard des Contributeurs GitHub ---")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (var entry : contributorsMap.entrySet()) {
            int rank = entry.getKey();
            String contributorName = entry.getValue().getKey();
            ContributorStats stats = entry.getValue().getValue();
            int addLines = stats.added();
            int removeLines = stats.removed();
            Component line = Component.text("\n#")
                    .color(getRankColor(rank))
                    .append(Component.text(rank).color(getRankColor(rank)))
                    .append(Component.text(" ").append(Component.text(contributorName).color(NamedTextColor.LIGHT_PURPLE)))
                    .append(Component.text(" + ").color(NamedTextColor.GREEN))
                    .append(Component.text(addLines).color(NamedTextColor.WHITE)
                            .append(Component.text(" - ").color(NamedTextColor.RED))
                            .append(Component.text(removeLines).color(NamedTextColor.WHITE)));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD));
        return text;
    }

    /**
     * Creates the leaderboard text for player money to be sent in chat or displayed as a hologram.
     *
     * @return A Component representing the player money leaderboard.
     */
    public static Component createMoneyTextLeaderboard() {
        var moneyMap = new TreeMap<>(LeaderboardManager.getPlayerMoneyMap());
        if (moneyMap.isEmpty()) {
            return Component.text("Aucun joueur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard de l'argent des joueurs ---")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (var entry : moneyMap.entrySet()) {
            int rank = entry.getKey();
            String playerName = entry.getValue().getKey();
            String money = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(getRankColor(rank))
                    .append(Component.text(rank).color(getRankColor(rank)))
                    .append(Component.text(" ").append(Component.text(playerName).color(NamedTextColor.LIGHT_PURPLE)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(money + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.WHITE));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD));
        return text;
    }

    /**
     * Creates the leaderboard text for playtime to be sent in chat or displayed as a hologram.
     *
     * @return A Component representing the playtime leaderboard.
     */
    public static Component createCityMoneyTextLeaderboard() {
        var moneyMap = LeaderboardManager.getVilleMoneyMap();
        if (moneyMap.isEmpty()) {
            return Component.text("Aucune ville trouvée pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard de l'argent des villes ---")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (var entry : moneyMap.entrySet()) {
            int rank = entry.getKey();
            String cityName = entry.getValue().getKey();
            String money = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(getRankColor(rank))
                    .append(Component.text(rank).color(getRankColor(rank)))
                    .append(Component.text(" ").append(Component.text(cityName).color(NamedTextColor.LIGHT_PURPLE)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(money + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.WHITE));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD));
        return text;
    }

    /**
     * Creates the leaderboard text for playtime to be sent in chat or displayed as a hologram.
     *
     * @return A Component representing the playtime leaderboard.
     */
    public static Component createPlayTimeTextLeaderboard() {
        var playtimeMap = new TreeMap<>(LeaderboardManager.getPlayTimeMap());
        if (playtimeMap.isEmpty()) {
            return Component.text("Aucun joueur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("------- Leaderboard du temps de jeu -------")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (var entry : playtimeMap.entrySet()) {
            int rank = entry.getKey();
            String playerName = entry.getValue().getKey();
            String time = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(getRankColor(rank))
                    .append(Component.text(rank).color(getRankColor(rank)))
                    .append(Component.text(" ").append(Component.text(playerName).color(NamedTextColor.LIGHT_PURPLE)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(time).color(NamedTextColor.WHITE));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD));
        return text;
    }

    public static Component createPumpkinCountTextLeaderboard() {
        var pumpkinCountMap = new TreeMap<>(LeaderboardManager.getPumpkinCountMap());
        if (pumpkinCountMap.isEmpty()) {
            return Component.text("Aucun joueur trouvé pour le moment.", NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard des critrouilles ---")
                .color(TextColor.color(156, 69, 26))
                .decorate(TextDecoration.BOLD);
        for (var entry : pumpkinCountMap.entrySet()) {
            int rank = entry.getKey();
            String playerName = entry.getValue().getKey();
            String pumpkinCount = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(getRankColor(rank))
                    .append(Component.text(rank).color(getRankColor(rank)))
                    .append(Component.text(" ").append(Component.text(playerName).color(TextColor.color(255, 107, 37))))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(pumpkinCount + " citrouilles").color(NamedTextColor.WHITE));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(TextColor.color(156, 69, 26))
                .decorate(TextDecoration.BOLD));
        return text;
    }

    /**
     * Retrieves the color associated with a specific rank.
     *
     * @param rank The rank for which the color is retrieved.
     * @return The TextColor associated with the rank.
     */
    public static TextColor getRankColor(int rank) {
        return switch (rank) {
            case 1 -> TextColor.color(0xFFD700);
            case 2 -> TextColor.color(0xC0C0C0);
            case 3 -> TextColor.color(0x614E1A);
            default -> TextColor.color(0x4B4B4B);
        };
    }

    public static void enable() {
        contributorsHologram = new TextDisplay(createContributorsTextLeaderboard(), contributorsHologramLocation, new Vector3f(scale));
        moneyHologram = new TextDisplay(createMoneyTextLeaderboard(), moneyHologramLocation, new Vector3f(scale));
        villeMoneyHologram = new TextDisplay(createCityMoneyTextLeaderboard(), villeMoneyHologramLocation, new Vector3f(scale));
        playTimeHologram = new TextDisplay(createPlayTimeTextLeaderboard(), playTimeHologramLocation, new Vector3f(scale));
        pumpkinCountHologram = new TextDisplay(createPumpkinCountTextLeaderboard(), pumpkinCountHologramLocation, new Vector3f(scale));
        taskTimer = new BukkitRunnable() {
            private int i = 0;

            @Override
            public void run() {
                if (i % 900 == 0)
                    updateGithubContributorsMap(); // toutes les 15 minutes pour ne pas être rate limitée par github
                if (i % 15 == 0) { // toutes les 15 secondes
                    updatePlayerMoneyMap();
                    updateCityMoneyMap();
                    updatePlayTimeMap();
                    updatePumpkinCountMap();
                    updateHolograms();
                }
                updateHologramsViewers();
                i++;
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0, 20L); // Toutes les 15 secondes en async sauf l'updateGithubContributorsMap qui est toutes les 30 minutes
    }

    public static void updateHologramsViewers() {
        if (contributorsHologramLocation != null) {
            contributorsHologram.updateViewersList();
        }
        if (moneyHologramLocation != null) {
            moneyHologram.updateViewersList();
        }
        if (villeMoneyHologramLocation != null) {
            villeMoneyHologram.updateViewersList();
        }
        if (playTimeHologramLocation != null) {
            playTimeHologram.updateViewersList();
        }
        if (pumpkinCountHologramLocation != null) {
            pumpkinCountHologram.updateViewersList();
        }
    }

    public static void disable() {
        taskTimer.cancel();
        contributorsHologram.remove();
        moneyHologram.remove();
        villeMoneyHologram.remove();
        playTimeHologram.remove();
        pumpkinCountHologram.remove();
    }

    /**
     * Sets the location of a hologram in the leaderboard configuration.
     *
     * @param name     The name of the hologram.
     * @param location The new location of the hologram.
     * @throws IOException If an error occurs while saving the configuration.
     */
    public static void setHologramLocation(String name, Location location) throws IOException {
        FileConfiguration leaderBoardConfig = YamlConfiguration.loadConfiguration(leaderBoardFile);
        leaderBoardConfig.set(name + "-location", location);
        leaderBoardConfig.save(leaderBoardFile);
        loadLeaderBoardConfig();
        if (contributorsHologram != null && name.equals("contributors")) {
            contributorsHologram.setLocation(location);
        } else if (moneyHologram != null && name.equals("money")) {
            moneyHologram.setLocation(location);
        } else if (villeMoneyHologram != null && name.equals("ville-money")) {
            villeMoneyHologram.setLocation(location);
        } else if (playTimeHologram != null && name.equals("playtime")) {
            playTimeHologram.setLocation(location);
        } else if (pumpkinCountHologram != null && name.equals("pumpkin-count")) {
            pumpkinCountHologram.setLocation(location);
        }
    }

    /**
     * Sets the scale of the holograms in the leaderboard configuration.
     *
     * @param scale The new scale of the holograms.
     * @throws IOException If an error occurs while saving the configuration.
     */
    public static void setScale(float scale) throws IOException {
        FileConfiguration leaderBoardConfig = YamlConfiguration.loadConfiguration(leaderBoardFile);
        leaderBoardConfig.set("scale", scale);
        leaderBoardConfig.save(leaderBoardFile);
        loadLeaderBoardConfig();
        if (contributorsHologram != null) {
            contributorsHologram.setScale(new Vector3f(scale));
        }
        if (moneyHologram != null) {
            moneyHologram.setScale(new Vector3f(scale));
        }
        if (villeMoneyHologram != null) {
            villeMoneyHologram.setScale(new Vector3f(scale));
        }
        if (playTimeHologram != null) {
            playTimeHologram.setScale(new Vector3f(scale));
        }
        if (pumpkinCountHologram != null) {
            pumpkinCountHologram.setScale(new Vector3f(scale));
        }
    }

    /**
     * Loads the leaderboard configuration, including hologram locations.
     */
    private static void loadLeaderBoardConfig() {
        if (!leaderBoardFile.exists()) {
            leaderBoardFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/leaderboards.yml", false);
        }
        FileConfiguration leaderBoardConfig = YamlConfiguration.loadConfiguration(leaderBoardFile);
        contributorsHologramLocation = leaderBoardConfig.getLocation("contributors-location");
        moneyHologramLocation = leaderBoardConfig.getLocation("money-location");
        villeMoneyHologramLocation = leaderBoardConfig.getLocation("ville-money-location");
        playTimeHologramLocation = leaderBoardConfig.getLocation("playtime-location");
        pumpkinCountHologramLocation = leaderBoardConfig.getLocation("pumpkin-count-location", new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
        scale = (float) leaderBoardConfig.getDouble("scale");
    }

    /**
     * Updates the GitHub contributors leaderboard map by fetching data from the GitHub API.
     * <a href="https://docs.github.com/fr/rest/metrics/statistics?apiVersion=2022-11-28#get-all-contributor-commit-activity">Documentation GitHub API (REST)</a>
     */
    public static void updateGithubContributorsMap() {
        String repoOwner = "ServerOpenMC";
        String repoName = "PluginV2";

        Set<String> realContributors = getRealContributors(repoOwner, repoName);

        fetchAndFilterContributorStats(repoOwner, repoName, realContributors);
    }

    private static Set<String> getRealContributors(String owner, String repo) {
        Set<String> contributors = new HashSet<>();
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contributors?per_page=100", owner, repo);

        try {
            HttpURLConnection con = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "OpenMC-BOT");

            if (con.getResponseCode() == 200) {
                JSONArray array = (JSONArray) new JSONParser().parse(new InputStreamReader(con.getInputStream()));

                for (Object obj : array) {
                    JSONObject contributor = (JSONObject) obj;
                    String login = (String) contributor.get("login");
                    String type = (String) contributor.get("type"); // "User" ou "Bot"

                    if (!"Bot".equals(type)) {
                        contributors.add(login);
                    }
                }
            }
            con.disconnect();
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Could not fetch contributors: {}", e.getMessage(), e);
        }

        return contributors;
    }

    private static void fetchAndFilterContributorStats(String owner, String repo, Set<String> allowedContributors) {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/stats/contributors", owner, repo);

        try {
            HttpURLConnection con = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "OpenMC-BOT");

            if (con.getResponseCode() == 200) {
                JSONArray statsArray = (JSONArray) new JSONParser().parse(new InputStreamReader(con.getInputStream()));
                List<Map.Entry<String, ContributorStats>> statsList = new ArrayList<>();

                for (Object obj : statsArray) {
                    JSONObject contributor = (JSONObject) obj;
                    JSONObject author = (JSONObject) contributor.get("author");
                    if (author == null) continue;

                    String login = (String) author.get("login");
                    if (!allowedContributors.contains(login)) continue;

                    // Calcul des contributions
                    JSONArray weeks = (JSONArray) contributor.get("weeks");
                    int totalNetLines = 0;
                    int totalAddLines = 0;
                    int totalRemoveLines = 0;

                    for (Object wObj : weeks) {
                        JSONObject week = (JSONObject) wObj;
                        totalNetLines += ((Long) week.get("a")).intValue() - ((Long) week.get("d")).intValue();
                        totalAddLines += ((Long) week.get("a")).intValue();
                        totalRemoveLines += ((Long) week.get("d")).intValue();
                    }

                    statsList.add(new AbstractMap.SimpleEntry<>(login, new ContributorStats(totalAddLines, totalRemoveLines)));
                }

                // Tri et affichage du classement
                statsList.sort((e1, e2) ->
                        Integer.compare(e2.getValue().getTotalLines(), e1.getValue().getTotalLines())
                );

                githubContributorsMap.clear();

                for (int i = 0; i < Math.min(10, statsList.size()); i++) {
                    githubContributorsMap.put(i + 1, statsList.get(i));
                }
            }
            con.disconnect();
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Could not fetch contributor stats: {}", e.getMessage(), e);
        }
    }

    /**
     * Updates the player money leaderboard map by sorting and formatting player balances.
     */
    public static void updatePlayerMoneyMap() {
        playerMoneyMap.clear();
        int rank = 1;

        Map<UUID, EconomyPlayer> balances = EconomyManager.getBalances();
        Map<UUID, Double> combinedBalances = new HashMap<>();
        balances.forEach((player, balance) -> combinedBalances.put(player, balance.getBalance()));

        BankManager.getBanks().forEach((uuid, bank) -> combinedBalances.merge(uuid, bank.getBalance(), Double::sum));

        for (var entry : combinedBalances.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(10)
                .toList()) {
            String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            String formattedBalance = EconomyManager.getFormattedSimplifiedNumber(entry.getValue());
            playerMoneyMap.put(rank++, new AbstractMap.SimpleEntry<>(playerName, formattedBalance));
        }
    }

    /**
     * Updates the city money leaderboard map by sorting and formatting city balances.
     */
    public static void updateCityMoneyMap() {
        villeMoneyMap.clear();
        int rank = 1;
        for (City city : CityManager.getCities().stream()
                .sorted((city1, city2) -> Double.compare(city2.getBalance(), city1.getBalance()))
                .limit(10)
                .toList()) {
            String cityName = city.getName();
            String cityBalance = EconomyManager.getFormattedSimplifiedNumber(city.getBalance());
            villeMoneyMap.put(rank++, new AbstractMap.SimpleEntry<>(cityName, cityBalance));
        }
    }

    /**
     * Updates the playtime leaderboard map by sorting and formatting player playtime.
     */
    public static void updatePlayTimeMap() {
        playTimeMap.clear();
        int rank = 1;
        for (OfflinePlayer player : Arrays.stream(Bukkit.getOfflinePlayers())
                .sorted((entry1, entry2) -> Long.compare(entry2.getStatistic(Statistic.PLAY_ONE_MINUTE), entry1.getStatistic(Statistic.PLAY_ONE_MINUTE)))
                .limit(10)
                .toList()) {
            String playerName = player.getName();
            String playTime = DateUtils.convertTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
            playTimeMap.put(rank++, new AbstractMap.SimpleEntry<>(playerName, playTime));
        }
    }

    public static void updatePumpkinCountMap() {
        pumpkinCountMap.clear();
        int rank = 1;

        Object2ObjectMap<UUID, HalloweenData> balances = HalloweenManager.getAllHalloweenData();
        System.out.println(balances);

        for (var entry : balances.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue().getPumpkinCount(), entry1.getValue().getPumpkinCount()))
                .limit(10)
                .toList()) {
            String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            String formattedPumpkinCount = EconomyManager.getFormattedSimplifiedNumber(entry.getValue().getPumpkinCount());
            pumpkinCountMap.put(rank++, new AbstractMap.SimpleEntry<>(playerName, formattedPumpkinCount));
        }
    }

    /**
     * Updates the holograms for all leaderboards by sending ENTITY_METADATA packets to players.
     */
    public static void updateHolograms() {
        if (contributorsHologram != null) {
            contributorsHologram.updateText(createContributorsTextLeaderboard());
        }
        if (moneyHologram != null) {
            moneyHologram.updateText(createMoneyTextLeaderboard());
        }
        if (villeMoneyHologram != null) {
            villeMoneyHologram.updateText(createCityMoneyTextLeaderboard());
        }
        if (playTimeHologram != null) {
            playTimeHologram.updateText(createPlayTimeTextLeaderboard());
        }
        if (pumpkinCountHologram != null) {
            pumpkinCountHologram.updateText(createPumpkinCountTextLeaderboard());
        }
    }

}

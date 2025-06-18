package fr.openmc.core.features.leaderboards;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.models.EconomyPlayer;
import fr.openmc.core.features.leaderboards.commands.LeaderboardCommands;
import fr.openmc.core.features.leaderboards.listeners.LeaderboardListener;
import fr.openmc.core.features.leaderboards.utils.PacketUtils;
import fr.openmc.core.utils.DateUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
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
    private static final Map<Integer, Map.Entry<String, Integer>> githubContributorsMap = new TreeMap<>();
    @Getter
    private static final Map<Integer, Map.Entry<String, String>> playerMoneyMap = new TreeMap<>();
    @Getter
    private static final Map<Integer, Map.Entry<String, String>> villeMoneyMap = new TreeMap<>();
    @Getter
    private static final Map<Integer, Map.Entry<String, String>> playTimeMap = new TreeMap<>();
    private static final File leaderBoardFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "leaderboards.yml");
    @Getter
    static ClientboundSetEntityDataPacket contributorsHologramMetadataPacket;
    @Getter
    static ClientboundSetEntityDataPacket moneyHologramMetadataPacket;
    @Getter
    static ClientboundSetEntityDataPacket villeMoneyHologramMetadataPacket;
    @Getter
    static ClientboundSetEntityDataPacket playtimeHologramMetadataPacket;
    @Getter
    private static Location contributorsHologramLocation;
    @Getter
    private static Location moneyHologramLocation;
    @Getter
    private static Location villeMoneyHologramLocation;
    @Getter
    private static Location playTimeHologramLocation;
    private static BukkitTask taskTimer;
    private static float scale;

    public LeaderboardManager() {
        loadLeaderBoardConfig();
        CommandsManager.getHandler().register(new LeaderboardCommands());
        new LeaderboardListener();
        enable();
    }

    /**
     * Creates the leaderboard text for GitHub contributors to be sent in chat or displayed as a hologram.
     *
     * @return A Component representing the GitHub contributors leaderboard.
     */
    public static Component createContributorsTextLeaderboard() {
        var contributorsMap = LeaderboardManager.getGithubContributorsMap();
        if (contributorsMap.isEmpty()) {
            return Component.text("Aucun contributeur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard des Contributeurs GitHub ---")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (var entry : contributorsMap.entrySet()) {
            int rank = entry.getKey();
            String contributorName = entry.getValue().getKey();
            int lines = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(getRankColor(rank))
                    .append(Component.text(rank).color(getRankColor(rank)))
                    .append(Component.text(" ").append(Component.text(contributorName).color(NamedTextColor.LIGHT_PURPLE)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(lines + " lignes crées").color(NamedTextColor.WHITE));
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
        var moneyMap = LeaderboardManager.getPlayerMoneyMap();
        if (moneyMap.isEmpty()) {
            return Component.text("Aucun joueur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard de l'argent des joueurs ----")
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
        Component text = Component.text("--- Leaderboard de l'argent des villes ----")
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
        var playtimeMap = LeaderboardManager.getPlayTimeMap();
        if (playtimeMap.isEmpty()) {
            return Component.text("Aucun joueur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard du temps de jeu -----------")
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
        taskTimer = new BukkitRunnable() {
            private int i = 0;

            @Override
            public void run() {
                if (i % 120 == 0)
                    updateGithubContributorsMap(); // toutes les 30 minutes pour ne pas être rate limitée par github
                updatePlayerMoneyMap();
                updateCityMoneyMap();
                updatePlayTimeMap();
                updateHolograms();
                i++;
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0, 300); // Toutes les 15 secondes en async sauf l'updateGithubContributorsMap qui est toutes les 30 minutes
        LeaderboardListener.getInstance().enable();
    }

    public static void disable() {
        taskTimer.cancel();
        LeaderboardListener.getInstance().disable();
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
        LeaderboardListener.getInstance().reload();
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
        LeaderboardListener.getInstance().reload();
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
        scale = (float) leaderBoardConfig.getDouble("scale");
    }

    /**
     * Updates the GitHub contributors leaderboard map by fetching data from the GitHub API.
     * Documentation GitHub API (REST) : https://docs.github.com/fr/rest/metrics/statistics?apiVersion=2022-11-28#get-all-contributor-commit-activity
     */
    private static void updateGithubContributorsMap() {
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
            OMCPlugin.getInstance().getLogger().warning("Erreur lors de la récupération de la liste des contributeurs: " + e.getMessage());
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
                List<Map.Entry<String, Integer>> statsList = new ArrayList<>();

                for (Object obj : statsArray) {
                    JSONObject contributor = (JSONObject) obj;
                    JSONObject author = (JSONObject) contributor.get("author");
                    if (author == null) continue;

                    String login = (String) author.get("login");
                    if (!allowedContributors.contains(login)) continue;

                    // Calcul des contributions
                    JSONArray weeks = (JSONArray) contributor.get("weeks");
                    int totalNetLines = 0;
                    for (Object wObj : weeks) {
                        JSONObject week = (JSONObject) wObj;
                        totalNetLines += ((Long) week.get("a")).intValue() - ((Long) week.get("d")).intValue();
                    }

                    if (totalNetLines > 0) {
                        statsList.add(new AbstractMap.SimpleEntry<>(login, totalNetLines));
                    }
                }

                // Tri et affichage du classement
                statsList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
                githubContributorsMap.clear();

                for (int i = 0; i < Math.min(10, statsList.size()); i++) {
                    githubContributorsMap.put(i + 1, statsList.get(i));
                }
            }
            con.disconnect();
        } catch (Exception e) {
            OMCPlugin.getInstance().getLogger().warning("Erreur lors de la récupération des stats: " + e.getMessage());
        }
    }

    /**
     * Updates the player money leaderboard map by sorting and formatting player balances.
     */
    private static void updatePlayerMoneyMap() {
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
    private static void updateCityMoneyMap() {
        villeMoneyMap.clear();
        int rank = 1;
        for (City city : CityManager.getCitiesByUUID().stream()
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
    private static void updatePlayTimeMap() {
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

    /**
     * Updates the holograms for all leaderboards by sending ENTITY_METADATA packets to players.
     */
    public static void updateHolograms() {
        if (contributorsHologramLocation != null) {
            String text = JSONComponentSerializer.json().serialize(createContributorsTextLeaderboard());
            contributorsHologramMetadataPacket = createMetadataPacket(text, -610329143);
            updateHologram(contributorsHologramLocation.getWorld().getPlayersSeeingChunk(contributorsHologramLocation.getChunk()), contributorsHologramMetadataPacket); // On met 100000 à l'id de l'entité pour pouvoir la modifier facilement
        }
        if (moneyHologramLocation != null) {
            String text = JSONComponentSerializer.json().serialize(createMoneyTextLeaderboard());
            moneyHologramMetadataPacket = createMetadataPacket(text, -102388303);
            updateHologram(moneyHologramLocation.getWorld().getPlayersSeeingChunk(moneyHologramLocation.getChunk()), moneyHologramMetadataPacket);
        }
        if (villeMoneyHologramLocation != null) {
            String text = JSONComponentSerializer.json().serialize(createCityMoneyTextLeaderboard());
            villeMoneyHologramMetadataPacket = createMetadataPacket(text, -699947630);
            updateHologram(villeMoneyHologramLocation.getWorld().getPlayersSeeingChunk(villeMoneyHologramLocation.getChunk()), villeMoneyHologramMetadataPacket);
        }
        if (playTimeHologramLocation != null) {
            String text = JSONComponentSerializer.json().serialize(createPlayTimeTextLeaderboard());
            playtimeHologramMetadataPacket = createMetadataPacket(text, -348090140);
            updateHologram(playTimeHologramLocation.getWorld().getPlayersSeeingChunk(playTimeHologramLocation.getChunk()), playtimeHologramMetadataPacket);
        }
    }

    /**
     * Creates a metadata packet for a text display hologram.
     *
     * @param text The text to display on the hologram.
     * @param id   The entity ID of the hologram.
     * @return A PacketContainer containing the metadata for the hologram.
     */
    private static ClientboundSetEntityDataPacket createMetadataPacket(String text, int id) {
        return PacketUtils.getSetEntityDataPacket(
                id,
                text,
                MinecraftServer.getServer().overworld(),
                new Vector3f(scale),
                net.minecraft.world.entity.Display.BillboardConstraints.VERTICAL,
                1,
                false,
                true,
                0.5f
        );
    }

    /**
     * Sends an ENTITY_METADATA packet to update the text of a hologram for a specific set of players.
     *
     * @param players The players who will receive the packet.
     */
    public static void updateHologram(Collection<Player> players, ClientboundSetEntityDataPacket metadataPacket) {
        if (players.isEmpty()) return;
        players.forEach(player -> {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(metadataPacket);
        });
    }

}

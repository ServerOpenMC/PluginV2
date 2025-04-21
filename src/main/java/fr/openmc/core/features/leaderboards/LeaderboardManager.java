package fr.openmc.core.features.leaderboards;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.leaderboards.Utils.PacketUtils;
import fr.openmc.core.features.leaderboards.commands.LeaderboardCommands;
import fr.openmc.core.features.leaderboards.listeners.PlayerListener;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
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
    private static LeaderboardManager instance;
    @Getter
    private final Map<Integer, Map.Entry<String, Integer>> githubContributorsMap = new TreeMap<>();
    @Getter
    private final Map<Integer, Map.Entry<String, String>> playerMoneyMap = new TreeMap<>();
    @Getter
    private final Map<Integer, Map.Entry<String, String>> villeMoneyMap = new TreeMap<>();
    @Getter
    private final Map<Integer, Map.Entry<String, String>> playTimeMap = new TreeMap<>();
    private final OMCPlugin plugin;
    private final String repoOwner = "ServerOpenMC";
    private final String repoName = "PluginV2";
    private final File leaderBoardFile;
    @Getter
    private Location contributorsHologramLocation;
    @Getter
    private Location moneyHologramLocation;
    @Getter
    private Location villeMoneyHologramLocation;
    @Getter
    private Location playTimeHologramLocation;


    public LeaderboardManager(OMCPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.leaderBoardFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "leaderboards.yml");
        loadLeaderBoardConfig();
        CommandsManager.getHandler().register(new LeaderboardCommands());
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(this), plugin);
        new BukkitRunnable() {
            private int i = 0;

            @Override
            public void run() {
                if (i % 60 == 0)
                    updateGithubContributorsMap(); // toutes les 5 minutes pour ne pas être rate limitée par github et parce que Margouta le demande
                updatePlayerMoneyMap();
                updateCityMoneyMap();
                updatePlayTimeMap();
                updateHolograms();
                i++;
            }
        }.runTaskTimerAsynchronously(plugin, 0, 100); // Toutes les 5 secondes en async sauf l'updateGithubContributorsMap qui est toutes les 5 minutes
    }

    /**
     * Converts a number of Minecraft ticks into a human-readable duration format.
     * The format includes days, hours, and minutes (e.g., "1j 2h 3m").
     *
     * @param ticks The number of ticks in Minecraft (20 ticks = 1 second).
     * @return A formatted string representing the duration in days, hours, and minutes.
     */
    private static String formatTicks(int ticks) {
        int seconds = ticks / 20;
        int days = seconds / 86400;
        int hours = (seconds % 86400) / 3600;
        int minutes = (seconds % 3600) / 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) result.append(days).append("j ");
        if (hours > 0) result.append(hours).append("h ");
        if (minutes > 0) result.append(minutes).append("m");

        return result.toString().trim();
    }

    /**
     * Creates the leaderboard text for GitHub contributors to be sent in chat or displayed as a hologram.
     *
     * @return A Component representing the GitHub contributors leaderboard.
     */
    public static Component createContributorsTextLeaderboard() {
        var contributorsMap = LeaderboardManager.getInstance().getGithubContributorsMap();
        if (contributorsMap.isEmpty()) {
            return Component.text("Aucun contributeur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard des Contributeurs GitHub ---")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (var entry : contributorsMap.entrySet()) {
            int rank = entry.getKey();
            String contributorName = entry.getValue().getKey();
            int contributions = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(getRankColor(rank))
                    .append(Component.text(rank).color(getRankColor(rank)))
                    .append(Component.text(" ").append(Component.text(contributorName).color(NamedTextColor.LIGHT_PURPLE)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(contributions).color(NamedTextColor.WHITE));
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
        var moneyMap = LeaderboardManager.getInstance().getPlayerMoneyMap();
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
        var moneyMap = LeaderboardManager.getInstance().getVilleMoneyMap();
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
        var playtimeMap = LeaderboardManager.getInstance().getPlayTimeMap();
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

    /**
     * Sets the location of a hologram in the leaderboard configuration.
     *
     * @param name     The name of the hologram.
     * @param location The new location of the hologram.
     * @throws IOException If an error occurs while saving the configuration.
     */
    public void setHologramLocation(String name, Location location) throws IOException {
        FileConfiguration leaderBoardConfig = YamlConfiguration.loadConfiguration(leaderBoardFile);
        leaderBoardConfig.set(name + "-location", location);
        leaderBoardConfig.save(leaderBoardFile);
        loadLeaderBoardConfig();
    }

    /**
     * Loads the leaderboard configuration, including hologram locations.
     */
    private void loadLeaderBoardConfig() {
        if (!leaderBoardFile.exists()) {
            leaderBoardFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/leaderboards.yml", false);
        }
        FileConfiguration leaderBoardConfig = YamlConfiguration.loadConfiguration(leaderBoardFile);
        contributorsHologramLocation = leaderBoardConfig.getLocation("contributors-location");
        moneyHologramLocation = leaderBoardConfig.getLocation("money-location");
        villeMoneyHologramLocation = leaderBoardConfig.getLocation("ville-money-location");
        playTimeHologramLocation = leaderBoardConfig.getLocation("playtime-location");
    }

    /**
     * Updates the GitHub contributors leaderboard map by fetching data from the GitHub API.
     */
    private void updateGithubContributorsMap() {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contributors", repoOwner, repoName);
        try {
            HttpURLConnection con = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "OpenMC-BOT");

            if (con.getResponseCode() != 200) return;

            JSONArray array = (JSONArray) new JSONParser().parse(new InputStreamReader(con.getInputStream()));
            con.disconnect();

            githubContributorsMap.clear();

            int count = 1;
            for (Object obj : array) {
                if (count > 10) return;
                JSONObject contributor = (JSONObject) obj;
                String login = (String) contributor.get("login");
                int contributions = ((Long) contributor.get("contributions")).intValue();
                var contributorStats = new AbstractMap.SimpleEntry<>(login, contributions);
                githubContributorsMap.put(count, contributorStats);
                count++;
            }
            // Code un peu compliqué à comprendre, mais il va juste faire une requête à l'api de github et mettre les infos dans la map

        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de la récupération des contributeurs GitHub: " + e.getMessage());
        }
    }

    /**
     * Updates the player money leaderboard map by sorting and formatting player balances.
     */
    private void updatePlayerMoneyMap() {
        playerMoneyMap.clear();
        int rank = 1;
        for (var entry : EconomyManager.getBalances().entrySet().stream()
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
    private void updateCityMoneyMap() {
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
    private void updatePlayTimeMap() {
        playTimeMap.clear();
        int rank = 1;
        for (OfflinePlayer player : Arrays.stream(Bukkit.getOfflinePlayers())
                .sorted((entry1, entry2) -> Long.compare(entry2.getStatistic(Statistic.PLAY_ONE_MINUTE), entry1.getStatistic(Statistic.PLAY_ONE_MINUTE)))
                .limit(10)
                .toList()) {
            String playerName = player.getName();
            String playTime = formatTicks(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
            playTimeMap.put(rank++, new AbstractMap.SimpleEntry<>(playerName, playTime));
        }
    }

    /**
     * Updates the holograms for all leaderboards by sending ENTITY_METADATA packets to players.
     */
    public void updateHolograms() {
        if (contributorsHologramLocation != null) {
            String text = JSONComponentSerializer.json().serialize(createContributorsTextLeaderboard());
            updateHologram(contributorsHologramLocation.getWorld().getPlayersSeeingChunk(contributorsHologramLocation.getChunk()), text, 100000); // On met 100000 à l'id de l'entité pour pouvoir la modifier facilement
        }
        if (moneyHologramLocation != null) {
            String text = JSONComponentSerializer.json().serialize(createMoneyTextLeaderboard());
            updateHologram(moneyHologramLocation.getWorld().getPlayersSeeingChunk(moneyHologramLocation.getChunk()), text, 100001);
        }
        if (villeMoneyHologramLocation != null) {
            String text = JSONComponentSerializer.json().serialize(createCityMoneyTextLeaderboard());
            updateHologram(villeMoneyHologramLocation.getWorld().getPlayersSeeingChunk(villeMoneyHologramLocation.getChunk()), text, 100002);
        }
        if (playTimeHologramLocation != null) {
            String text = JSONComponentSerializer.json().serialize(createPlayTimeTextLeaderboard());
            updateHologram(playTimeHologramLocation.getWorld().getPlayersSeeingChunk(playTimeHologramLocation.getChunk()), text, 100003);
        }
    }

    /**
     * Sends an ENTITY_METADATA packet to update the text of a hologram for a specific set of players.
     *
     * @param players The players who will receive the packet.
     * @param text    The text to display on the hologram.
     * @param id      The entity ID of the hologram.
     */
    private void updateHologram(Collection<Player> players, String text, int id) {
        if (players.isEmpty()) return;
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer metadataPacket = PacketUtils.getTextDisplayMetadataPacket(
                id,
                text,
                Integer.MAX_VALUE, // Taille maximale du texte avant le retour à la ligne
                0x40000000, // Arrière-plan (valeur par défaut)
                10, // Durée d'interpolation (10 ticks)
                (byte) 1, // L'orientation 0 = FIXED, 1 = VERTICAL, 2 = HORIZONTAL, 3 = CENTER
                new Display.Brightness(15, 15),
                0.5f, // Entre 16 et 160 blocs de distance max (ça dépend des paramètres du client). 32 blocs par défaut
                1, // L'alignement 0 = CENTER, 1 = LEFT, 2 = RIGHT
                false // Si le texte est visible à travers les blocs
        );
        manager.broadcastServerPacket(
                metadataPacket,
                players
        );
    }

}

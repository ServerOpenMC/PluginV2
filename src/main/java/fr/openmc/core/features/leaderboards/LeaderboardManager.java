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
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
    private final Map<Integer, Map.Entry<String, Integer>> GithubContributorsMap = new TreeMap<>();
    @Getter
    private final Map<Integer, Map.Entry<String, String>> PlayerMoneyMap = new TreeMap<>();
    @Getter
    private final Map<Integer, Map.Entry<String, String>> VilleMoneyMap = new TreeMap<>();
    @Getter
    private final Map<Integer, Map.Entry<String, String>> PlayTimeMap = new TreeMap<>();
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
                if (i % 12 == 0)
                    updateGithubContributorsMap(); // toutes les minutes pour ne pas être rate limitée par github
                updatePlayerMoneyMap();
                updateVilleMoneyMap();
                updatePlayTimeMap();
                updateHolograms();
                i++;
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20 * 5); //Toutes les 5 secondes en async sauf le updateGithubContributorsMap qui est toutes les minutes
    }

    public void setHologramLocation(String name, Location location) throws IOException {
        FileConfiguration leaderBoardConfig = YamlConfiguration.loadConfiguration(leaderBoardFile);
        leaderBoardConfig.set(name+"-location", location);
        leaderBoardConfig.save(leaderBoardFile);
        loadLeaderBoardConfig();
    }

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

    public static Component createContributorsTextLeaderboard() {
        var contributorsMap = LeaderboardManager.getInstance().getGithubContributorsMap();
        if (contributorsMap.isEmpty()) {
            return Component.text("Aucun contributeur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard des Contributeurs GitHub ---")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        for (var entry : contributorsMap.entrySet()) {
            int rank = entry.getKey();
            String contributorName = entry.getValue().getKey();
            int contributions = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(NamedTextColor.YELLOW)
                    .append(Component.text(rank).color(NamedTextColor.YELLOW))
                    .append(Component.text(" ").append(Component.text(contributorName).color(NamedTextColor.GREEN)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(contributions + " contributions").color(NamedTextColor.AQUA));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        return text;
    }

    public static Component createMoneyTextLeaderboard() {
        var moneyMap = LeaderboardManager.getInstance().getPlayerMoneyMap();
        if (moneyMap.isEmpty()) {
            return Component.text("Aucun joueur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard de l'argent des joueurs ----")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        for (var entry : moneyMap.entrySet()) {
            int rank = entry.getKey();
            String playerName = entry.getValue().getKey();
            String money = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(NamedTextColor.YELLOW)
                    .append(Component.text(rank).color(NamedTextColor.YELLOW))
                    .append(Component.text(" ").append(Component.text(playerName).color(NamedTextColor.GREEN)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(money + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.AQUA));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        return text;
    }

    public static Component createCityMoneyTextLeaderboard() {
        var moneyMap = LeaderboardManager.getInstance().getVilleMoneyMap();
        if (moneyMap.isEmpty()) {
            return Component.text("Aucune ville trouvée pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard de l'argent des villes ----")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        for (var entry : moneyMap.entrySet()) {
            int rank = entry.getKey();
            String cityName = entry.getValue().getKey();
            String money = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(NamedTextColor.YELLOW)
                    .append(Component.text(rank).color(NamedTextColor.YELLOW))
                    .append(Component.text(" ").append(Component.text(cityName).color(NamedTextColor.GREEN)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(money + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.AQUA));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        return text;
    }

    public static Component createPlayTimeTextLeaderboard() {
        var playtimeMap = LeaderboardManager.getInstance().getPlayTimeMap();
        if (playtimeMap.isEmpty()) {
            return Component.text("Aucun joueur trouvé pour le moment.").color(NamedTextColor.RED);
        }
        Component text = Component.text("--- Leaderboard du temps de jeu -----------")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        for (var entry : playtimeMap.entrySet()) {
            int rank = entry.getKey();
            String playerName = entry.getValue().getKey();
            String time = entry.getValue().getValue();
            Component line = Component.text("\n#")
                    .color(NamedTextColor.YELLOW)
                    .append(Component.text(rank).color(NamedTextColor.YELLOW))
                    .append(Component.text(" ").append(Component.text(playerName).color(NamedTextColor.GREEN)))
                    .append(Component.text(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text(time).color(NamedTextColor.AQUA));
            text = text.append(line);
        }
        text = text.append(Component.text("\n-----------------------------------------")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        return text;
    }

    @SneakyThrows
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

    private void updateGithubContributorsMap() {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contributors", repoOwner, repoName);
        try {
            HttpURLConnection con = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "OpenMC-Agent");

            if (con.getResponseCode() != 200) return;

            JSONArray array = (JSONArray) new JSONParser().parse(new InputStreamReader(con.getInputStream()));
            con.disconnect();

            GithubContributorsMap.clear();

            int count = 1;
            for (Object obj : array) {
                if (count > 10) return;
                JSONObject contributor = (JSONObject) obj;
                String login = (String) contributor.get("login");
                int contributions = ((Long) contributor.get("contributions")).intValue();
                var contributorStats = new AbstractMap.SimpleEntry<>(login, contributions);
                GithubContributorsMap.put(count, contributorStats);
                count++;
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de la récupération des contributeurs GitHub: " + e.getMessage()); // ne mérite pas severe je pense
        }
    }

    private void updatePlayerMoneyMap() {
        PlayerMoneyMap.clear();
        int rank = 1;
        for (var entry : EconomyManager.getBalances().entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(10)
                .toList()) {
            String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            String formattedBalance = EconomyManager.getFormattedSimplifiedNumber(entry.getValue());
            PlayerMoneyMap.put(rank++, new AbstractMap.SimpleEntry<>(playerName, formattedBalance));
        }
    }

    private void updateVilleMoneyMap() {
        VilleMoneyMap.clear();
        int rank = 1;
        for (City city : CityManager.getCities().stream()
                .sorted((city1, city2) -> Double.compare(city2.getBalance(), city1.getBalance()))
                .limit(10)
                .toList()) {
            String cityName = city.getName();
            String cityBalance = EconomyManager.getFormattedSimplifiedNumber(city.getBalance());
            VilleMoneyMap.put(rank++, new AbstractMap.SimpleEntry<>(cityName, cityBalance));
        }
    }

    private void updatePlayTimeMap() {
        PlayTimeMap.clear();
        int rank = 1;
        for (OfflinePlayer player : Arrays.stream(Bukkit.getOfflinePlayers())
                .sorted((entry1, entry2) -> Long.compare(entry2.getStatistic(Statistic.PLAY_ONE_MINUTE), entry1.getStatistic(Statistic.PLAY_ONE_MINUTE)))
                .limit(10)
                .toList()) {
            String playerName = player.getName();
            String playTime = formatTicks(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
            PlayTimeMap.put(rank++, new AbstractMap.SimpleEntry<>(playerName, playTime));
        }
    }

    public void updateHolograms() {
        if (contributorsHologramLocation != null) {
            String text = LegacyComponentSerializer.legacySection().serialize(createContributorsTextLeaderboard());
            updateHologram(contributorsHologramLocation.getWorld().getPlayers(), text,100000); // On met 100000 à l'id de l'entité pour pouvoir la modifier facilement
        }
        if (moneyHologramLocation != null) {
            String text = LegacyComponentSerializer.legacySection().serialize(createMoneyTextLeaderboard());
            updateHologram(moneyHologramLocation.getWorld().getPlayers(), text, 100001);
        }
        if (villeMoneyHologramLocation != null) {
            String text = LegacyComponentSerializer.legacySection().serialize(createCityMoneyTextLeaderboard());
            updateHologram(villeMoneyHologramLocation.getWorld().getPlayers(), text, 100002);
        }
        if (playTimeHologramLocation != null) {
            String text = LegacyComponentSerializer.legacySection().serialize(createPlayTimeTextLeaderboard());
            updateHologram(playTimeHologramLocation.getWorld().getPlayers(), text, 100003);
        }
    }

    private void updateHologram(Collection<Player> players, String text, int id) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer metadataPacket = PacketUtils.getTextDisplayMetadataPacket(
                id,
                text,
                Integer.MAX_VALUE, // Taille maximale du texte avant le retour à la ligne
                0x40000000, // Arrière-plan (valeur par défaut)
                10, // Durée d'interpolation (10 ticks)
                (byte) 1, // L'orientation 0 = FIXED, 1 = VERTICAL, 2 = HORIZONTAL, 3 = CENTER
                new Display.Brightness(15,15),
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

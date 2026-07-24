package fr.openmc.core.hooks.github;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.hooks.Hooks;
import fr.openmc.core.bootstrap.hooks.HttpsHook;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.hooks.github.models.ContributorStats;
import fr.openmc.core.hooks.github.models.DBGithubMinecraft;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GitHubHook extends HttpsHook implements HasDatabase {
    public final static String REPO_OWNER = "ServerOpenMC";
    public final static String REPO_NAME = "PluginV2";

    private static Dao<DBGithubMinecraft, String> linkGithubMinecraft;

    public static final Map<UUID, DBGithubMinecraft> playerGithubMap = new ConcurrentHashMap<>();
    // * Map reliant nom contributeur -> à ses Stats
    private static final Map<String, ContributorStats> contributorStatsMap = new ConcurrentHashMap<>();

    @Override
    public void init() {
        fetchContributorStats();
        loadAllPlayerLinkGithubData();
    }

    @Override
    public void save() {
        saveAllPlayerLinkGithubData();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBGithubMinecraft.class);
        linkGithubMinecraft = DaoManager.createDao(connectionSource, DBGithubMinecraft.class);
    }

    @Override
    public String getName() {
        return "GitHubHook";
    }

    public static boolean isEnable() {
        return Hooks.isEnabled(GitHubHook.class);
    }

    private static void loadAllPlayerLinkGithubData() {
        try {
            playerGithubMap.clear();
            linkGithubMinecraft.queryForAll().forEach(playerData -> {
                playerGithubMap.put(playerData.getPlayerUUID(), playerData);
                try {
                    linkGithubMinecraft.delete(playerData);
                } catch (SQLException e) {
                    OMCLogger.error("Cannot load player link github data", e);
                }
            });
        } catch (SQLException e) {
            OMCLogger.error("Cannot load player link github data", e);
        }
    }

    public static void saveAllPlayerLinkGithubData() {
        playerGithubMap.forEach((uuid, playerSave) -> {
            try {
                linkGithubMinecraft.createOrUpdate(playerSave);
            } catch (SQLException e) {
                OMCLogger.error("Cannot save player link github data for player {}", uuid, e);
            }
        });
    }

    public static Map<Integer, String> getContributors() {
        Map<Integer, String> contributors = new HashMap<>();
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contributors?per_page=100",
                REPO_OWNER, REPO_NAME);

        try {
            HttpURLConnection con = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "OpenMC-BOT");

            // si la réponse est pas accepté
            if (con.getResponseCode() != 200) {
                con.disconnect();
                return contributors;
            }

            JSONArray array = (JSONArray) new JSONParser().parse(new InputStreamReader(con.getInputStream()));

            for (Object obj : array) {
                JSONObject contributor = (JSONObject) obj;
                String nameContributor = (String) contributor.get("login");
                Integer idContributor = (Integer) contributor.get("id");
                String type = (String) contributor.get("type"); // "User" ou "Bot"

                if ("Bot".equals(type)) continue;

                contributors.put(idContributor, nameContributor);
            }

            con.disconnect();
        } catch (Exception e) {
            OMCLogger.warn("Could not fetch contributors: {}", e.getMessage(), e);
        }

        return contributors;
    }

    /**
     * Mets à jours les stats des contributeurs dans la map contributorStatsMap
     */
    public static void fetchContributorStats() {
        Collection<String> contributors = getContributors().values();
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/stats/contributors",
                REPO_OWNER, REPO_NAME);

        try {
            HttpURLConnection con = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "OpenMC-BOT");

            if (con.getResponseCode() != 200) {
                con.disconnect();
                return;
            }

            JSONArray statsArray = (JSONArray) new JSONParser().parse(new InputStreamReader(con.getInputStream()));
            Map<String, ContributorStats> newStats = new ConcurrentHashMap<>();

            for (Object obj : statsArray) {
                JSONObject contributor = (JSONObject) obj;
                JSONObject author = (JSONObject) contributor.get("author");
                if (author == null) continue;

                String nameContributor = (String) author.get("login");
                if (!contributors.contains(nameContributor)) continue;

                JSONArray weeks = (JSONArray) contributor.get("weeks");
                int totalAddLines = 0;
                int totalRemoveLines = 0;

                for (Object wObj : weeks) {
                    JSONObject week = (JSONObject) wObj;
                    totalAddLines += ((Long) week.get("a")).intValue();
                    totalRemoveLines += ((Long) week.get("d")).intValue();
                }

                newStats.put(nameContributor, new ContributorStats(totalAddLines, totalRemoveLines));
            }

            contributorStatsMap.clear();
            contributorStatsMap.putAll(newStats);

            con.disconnect();
        } catch (Exception e) {
            OMCLogger.warn("Could not fetch contributor stats: {}", e.getMessage(), e);
        }
    }

    public static void linkPlayerToContributor(UUID playerUUID, int idGithub) {
        playerGithubMap.put(playerUUID, new DBGithubMinecraft(playerUUID, idGithub));
        fetchContributorStats();
    }

    public static void unlinkPlayerToContributor(UUID playerUUID) {
        playerGithubMap.remove(playerUUID);
        fetchContributorStats();
    }

    public static ContributorStats getStats(String nameGithub) {
        return contributorStatsMap.get(nameGithub);
    }

    public static int getTotalLines(String nameGithub) {
        ContributorStats stats = contributorStatsMap.get(nameGithub);
        return stats == null ? 0 : stats.getTotalLines();
    }

    public static int getTotalAddedLines(String nameGithub) {
        ContributorStats stats = contributorStatsMap.get(nameGithub);
        return stats == null ? 0 : stats.totalAddLines();
    }

    public static int getTotalRemovedLines(String nameGithub) {
        ContributorStats stats = contributorStatsMap.get(nameGithub);
        return stats == null ? 0 : stats.totalRemoveLines();
    }

    public static String getContributorName(int idGithub) {
        return getContributors().getOrDefault(idGithub, "null");
    }

    public static int getContributorId(String nameGithub) {
        return getContributors().entrySet().stream()
                .filter(entry -> entry.getValue().equals(nameGithub))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    public static ContributorStats getStats(int idGithub) {
        return contributorStatsMap.get(getContributorName(idGithub));
    }

    public static int getTotalLines(int idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.getTotalLines();
    }

    public static int getTotalAddedLines(int idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.totalAddLines();
    }

    public static int getTotalRemovedLines(int idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.totalRemoveLines();
    }

    public static DBGithubMinecraft getContributorLink(UUID uuid) {
        return playerGithubMap.get(uuid);
    }

    public static UUID getPlayerLinkTo(int idGithub) {
        return playerGithubMap.values().stream()
                .filter(data -> data.getGithubID() == idGithub)
                .map(DBGithubMinecraft::getPlayerUUID)
                .findFirst()
                .orElse(null);
    }
}

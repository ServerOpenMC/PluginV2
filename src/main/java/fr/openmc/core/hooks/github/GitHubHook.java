package fr.openmc.core.hooks.github;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.hooks.Hooks;
import fr.openmc.core.bootstrap.hooks.HttpsHook;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.toor.InternalToorApiClient;
import fr.openmc.core.hooks.github.models.ContributorStats;
import fr.openmc.core.hooks.github.models.DBGithubMinecraft;
import fr.openmc.core.utils.cache.TtlCache;
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
import java.util.concurrent.TimeUnit;

public class GitHubHook extends HttpsHook implements HasDatabase {
    public final static String REPO_OWNER = "ServerOpenMC";
    public final static String REPO_NAME = "PluginV2";

    private static Dao<DBGithubMinecraft, String> linkGithubMinecraft;

    public static final Map<UUID, DBGithubMinecraft> lastKnownLinkMap = new ConcurrentHashMap<>();
    private static final Map<String, ContributorStats> contributorStatsMap = new ConcurrentHashMap<>();

    private static final TtlCache<UUID, Long> githubLinkCache = new TtlCache<>(60, TimeUnit.SECONDS);
    private static final TtlCache<Long, String> usernameCache = new TtlCache<>(10, TimeUnit.MINUTES);

    @Override
    public void init() {
        loadAllPlayerLinkGithubData();
        fetchContributorStats();
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
            lastKnownLinkMap.clear();
            for (DBGithubMinecraft link : linkGithubMinecraft.queryForAll()) {
                lastKnownLinkMap.put(link.getPlayerUUID(), link);
            }
        } catch (SQLException e) {
            OMCLogger.error("Cannot load player link github data", e);
        }
    }

    public static Long getContributorId(UUID playerUUID) {
        if (githubLinkCache.contains(playerUUID)) return githubLinkCache.get(playerUUID);

        InternalToorApiClient.GithubStatus status = InternalToorApiClient.checkGithubStatus(playerUUID);

        if (status.linked()) {
            Long githubId = status.githubUserId();
            githubLinkCache.put(playerUUID, githubId);
            persistFallback(playerUUID, githubId);
            return githubId;
        }

        githubLinkCache.put(playerUUID, null);
        removeFallback(playerUUID);
        return null;
    }

    private static void persistFallback(UUID playerUUID, long githubId) {
        DBGithubMinecraft link = new DBGithubMinecraft(playerUUID, githubId);
        lastKnownLinkMap.put(playerUUID, link);
        try {
            linkGithubMinecraft.createOrUpdate(link);
        } catch (SQLException e) {
            OMCLogger.error("Cannot persist github link fallback for {}", playerUUID, e);
        }
    }

    private static void removeFallback(UUID playerUUID) {
        if (lastKnownLinkMap.remove(playerUUID) != null) {
            try {
                linkGithubMinecraft.deleteById(playerUUID.toString());
            } catch (SQLException e) {
                OMCLogger.error("Cannot clear github link fallback for {}", playerUUID, e);
            }
        }
    }

    public static Long refreshContributorId(UUID playerUUID) {
        githubLinkCache.invalidate(playerUUID);
        return getContributorId(playerUUID);
    }

    public static Map<Long, String> getContributors() {
        Map<Long, String> contributors = new HashMap<>();
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
                Long idContributor = (Long) contributor.get("id");
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

    public static String getContributorName(long idGithub) {
        return getContributors().getOrDefault(idGithub, "null");
    }

    public static ContributorStats getStats(long idGithub) {
        return contributorStatsMap.get(getContributorName(idGithub));
    }

    public static int getTotalLines(long idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.getTotalLines();
    }

    public static int getTotalAddedLines(long idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.totalAddLines();
    }

    public static int getTotalRemovedLines(long idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.totalRemoveLines();
    }

    public static DBGithubMinecraft getContributorLink(UUID uuid) {
        Long githubId = getContributorId(uuid);
        if (githubId == null) return null;
        return lastKnownLinkMap.getOrDefault(uuid, new DBGithubMinecraft(uuid, githubId));
    }

    public static UUID getPlayerLinkTo(long idGithub) {
        return lastKnownLinkMap.values().stream()
                .filter(data -> data.getGithubID() == idGithub)
                .map(DBGithubMinecraft::getPlayerUUID)
                .findFirst()
                .orElse(null);
    }

    public static Collection<DBGithubMinecraft> getKnownLinks() {
        return lastKnownLinkMap.values();
    }

    public static String getUsernameById(long githubId) {
        return usernameCache.getOrCompute(githubId, InternalToorApiClient::getGithubUsername);
    }
}

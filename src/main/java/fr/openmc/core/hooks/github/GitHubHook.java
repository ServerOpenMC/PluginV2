package fr.openmc.core.hooks.github;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.toor.InternalToorApiClient;
import fr.openmc.core.hooks.github.models.ContributorStats;
import fr.openmc.core.hooks.github.models.DBGithubMinecraft;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.hooks.Hooks;
import fr.openmc.core.registry.hooks.HttpsHook;
import fr.openmc.core.utils.cache.TtlCache;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GitHubHook extends HttpsHook implements HasDatabase {
    public static final String REPO_OWNER = "ServerOpenMC";
    public static final String REPO_NAME = "PluginV2";

    private final DBTable<DBGithubMinecraft, String> linkGithubMinecraftTable = DBTable.of(DBGithubMinecraft.class);

    public final Map<UUID, DBGithubMinecraft> lastKnownLinkMap = new ConcurrentHashMap<>();
    private final Map<String, ContributorStats> contributorStatsMap = new ConcurrentHashMap<>();

    private final TtlCache<UUID, Long> githubLinkCache = new TtlCache<>(60, TimeUnit.SECONDS);
    private final TtlCache<Long, String> usernameCache = new TtlCache<>(10, TimeUnit.MINUTES);

    @Override
    public void init() {
        loadAllPlayerLinkGithubData();
        fetchContributorStats();
    }

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(linkGithubMinecraftTable);
    }

    @Override
    public String getName() {
        return "GitHubHook";
    }

    @Override
    public boolean isEnable() {
        return Hooks.isEnabled(GitHubHook.class);
    }

    private void loadAllPlayerLinkGithubData() {
        lastKnownLinkMap.clear();
        lastKnownLinkMap.putAll(linkGithubMinecraftTable.loadMap(DBGithubMinecraft::getPlayerUUID));
    }

    public Long getContributorId(UUID playerUUID) {
        if (githubLinkCache.contains(playerUUID)) return githubLinkCache.get(playerUUID);

        InternalToorApiClient.GithubStatus status = OMCRegistry.FEATURES.DISCORD_LINK.get().toorApiClient.checkGithubStatus(playerUUID);

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

    private void persistFallback(UUID playerUUID, long githubId) {
        DBGithubMinecraft link = new DBGithubMinecraft(playerUUID, githubId);
        lastKnownLinkMap.put(playerUUID, link);
        try {
            linkGithubMinecraftTable.createOrUpdate(link);
        } catch (SQLException e) {
            OMCLogger.error("Cannot persist github link fallback for {}", playerUUID, e);
        }
    }

    private void removeFallback(UUID playerUUID) {
        if (lastKnownLinkMap.remove(playerUUID) != null) {
            try {
                linkGithubMinecraftTable.deleteById(playerUUID.toString());
            } catch (SQLException e) {
                OMCLogger.error("Cannot clear github link fallback for {}", playerUUID, e);
            }
        }
    }

    public Long refreshContributorId(UUID playerUUID) {
        githubLinkCache.invalidate(playerUUID);
        return getContributorId(playerUUID);
    }

    public Map<Long, String> getContributors() {
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
    public void fetchContributorStats() {
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

    public ContributorStats getStats(String nameGithub) {
        return contributorStatsMap.get(nameGithub);
    }

    public int getTotalLines(String nameGithub) {
        ContributorStats stats = contributorStatsMap.get(nameGithub);
        return stats == null ? 0 : stats.getTotalLines();
    }

    public int getTotalAddedLines(String nameGithub) {
        ContributorStats stats = contributorStatsMap.get(nameGithub);
        return stats == null ? 0 : stats.totalAddLines();
    }

    public int getTotalRemovedLines(String nameGithub) {
        ContributorStats stats = contributorStatsMap.get(nameGithub);
        return stats == null ? 0 : stats.totalRemoveLines();
    }

    public String getContributorName(long idGithub) {
        return getContributors().getOrDefault(idGithub, "null");
    }

    public ContributorStats getStats(long idGithub) {
        return contributorStatsMap.get(getContributorName(idGithub));
    }

    public int getTotalLines(long idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.getTotalLines();
    }

    public int getTotalAddedLines(long idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.totalAddLines();
    }

    public int getTotalRemovedLines(long idGithub) {
        ContributorStats stats = contributorStatsMap.get(getContributorName(idGithub));
        return stats == null ? 0 : stats.totalRemoveLines();
    }

    public DBGithubMinecraft getContributorLink(UUID uuid) {
        Long githubId = getContributorId(uuid);
        if (githubId == null) return null;
        return lastKnownLinkMap.getOrDefault(uuid, new DBGithubMinecraft(uuid, githubId));
    }

    public UUID getPlayerLinkTo(long idGithub) {
        return lastKnownLinkMap.values().stream()
                .filter(data -> data.getGithubID() == idGithub)
                .map(DBGithubMinecraft::getPlayerUUID)
                .findFirst()
                .orElse(null);
    }

    public Collection<DBGithubMinecraft> getKnownLinks() {
        return lastKnownLinkMap.values();
    }

    public String getUsernameById(long githubId) {
        return usernameCache.getOrCompute(githubId, OMCRegistry.FEATURES.DISCORD_LINK.get().toorApiClient::getGithubUsername);
    }
}

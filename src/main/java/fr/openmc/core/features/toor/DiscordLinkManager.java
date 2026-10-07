package fr.openmc.core.features.toor;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.bits.BitsManager;
import fr.openmc.core.features.toor.commands.LinkCommand;
import fr.openmc.core.features.toor.commands.UnlinkCommand;
import fr.openmc.core.features.toor.event.ConnectToDiscordEvent;
import fr.openmc.core.features.toor.models.DBDiscordLink;
import fr.openmc.core.features.toor.utils.RequestSigner;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.utils.cache.TtlCache;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DiscordLinkManager extends Feature implements HasDatabase, HasCommands {
    private final BitsManager bitsManager = OMCRegistry.FEATURES.BITS.get();
    private final GitHubHook gitHubHook = OMCRegistry.HOOKS.GITHUB;

    private Dao<DBDiscordLink, UUID> discordLinksDao;
    private final Map<UUID, DBDiscordLink> linkCache = new ConcurrentHashMap<>();

    // Map<code, (Player, expireAt, task)>
    private final Map<String, PendingLink> pendingLinks = new ConcurrentHashMap<>();
    private final TtlCache<String, String> discordUsernameCache = new TtlCache<>(10, TimeUnit.MINUTES);

    public final InternalToorApiClient toorApiClient = new InternalToorApiClient(this);

    private final long CODE_TTL_MS = 10 * 60 * 1000;
    private final long POLL_INTERNAL_TICKS = 20L * 3;

    @Getter
    private String botUrl = "http://localhost:3000";

    private record PendingLink(UUID playerUUID, long expiresAt, BukkitTask pollTask) { }

    @Override
    protected void onEnable() {
        linkCache.clear();
        loadConfig();
        loadAll();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBDiscordLink.class);
        discordLinksDao = DaoManager.createDao(connectionSource, DBDiscordLink.class);
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new LinkCommand(),
                new UnlinkCommand()
        );
    }

    private void loadConfig() {
        File dataFolder = OMCPlugin.getInstance().getDataFolder();
        File configFile = new File(dataFolder, "data/discord/discord.yml");
        File defaultKeyFile = new File(dataFolder, "data/discord/plugin_private.pem");

        if (!configFile.exists()) {
            OMCLogger.warn("discord.yml introuvable a {}", configFile.getPath());
            RequestSigner.init(defaultKeyFile.toPath());
            return;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(configFile);

        botUrl = yaml.getString("internal-api.bot-url", botUrl);

        String keyPathStr = yaml.getString("internal-api.private-key-path", "data/discord/plugin_private.pem");
        File keyFile = new File(dataFolder, keyPathStr);
        RequestSigner.init(keyFile.toPath());
    }

    private void loadAll() {
        try {
            for (DBDiscordLink link : discordLinksDao.queryForAll()) {
                linkCache.put(link.getPlayerUUID(), link);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLinked(UUID playerUUID) {
        return linkCache.containsKey(playerUUID);
    }

    public String startLink(Player player) {
        UUID playerUUID = player.getUniqueId();
        cancelPendingFor(playerUUID);

        InternalToorApiClient.LinkRequestResult result = toorApiClient.requestLinkCode(playerUUID, player.getName());
        if (!result.success()) return null;

        String code = result.code();
        BukkitTask pollTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                OMCPlugin.getInstance(),
                () -> pollCode(code, playerUUID),
                POLL_INTERNAL_TICKS,
                POLL_INTERNAL_TICKS
        );

        pendingLinks.put(code, new PendingLink(playerUUID, System.currentTimeMillis() + CODE_TTL_MS, pollTask));
        return code;
    }

    private void pollCode(String code, UUID playerUUID) {
        PendingLink pending = pendingLinks.get(code);
        if (pending == null) return;

        if (System.currentTimeMillis() > pending.expiresAt()) {
            cancelPendingFor(playerUUID);
            notifyPlayer(playerUUID, "feature.discord.expired", MessageType.ERROR);
            return;
        }

        InternalToorApiClient.LinkStatus status = toorApiClient.checkLinkStatus(code);
        if (!status.linked()) return;

        String discordUserId = status.discordUserId();
        String discordUsername = status.discordUsername();

        confirmLink(playerUUID, status.discordUserId());
        toorApiClient.consumeCode(code);
        cancelPendingFor(playerUUID);
        notifyPlayer(playerUUID, "feature.discord.success", MessageType.SUCCESS, Component.text(discordUsername));
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(),
                () -> Bukkit.getPluginManager().callEvent(new ConnectToDiscordEvent(playerUUID, discordUserId, discordUsername)));
        Long githubId = gitHubHook.getContributorId(playerUUID);
        if (githubId != null)
            bitsManager.applyContributorBitsUpdate(githubId);
    }

    private void confirmLink(UUID playerUUID, String discordUserId) {
        DBDiscordLink link = new DBDiscordLink(playerUUID, discordUserId);
        linkCache.put(playerUUID, link);
        try {
            discordLinksDao.createOrUpdate(link);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyPlayer(UUID playerUUID, String translationKey, MessageType type, ComponentLike... args) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                MessagesManager.sendMessage(player, TranslationManager.translation(translationKey, args), Prefix.OPENMC, type, true);
            }
        });
    }

    private void cancelPendingFor(UUID playerUUID) {
        pendingLinks.entrySet().removeIf(entry -> {
            if (entry.getValue().playerUUID().equals(playerUUID)) {
                entry.getValue().pollTask().cancel();
                return true;
            }
            return false;
        });
    }

    public boolean unlink(UUID playerUUID) {
        if (!linkCache.containsKey(playerUUID)) return false;

        cancelPendingFor(playerUUID);
        linkCache.remove(playerUUID);

        try {
            discordLinksDao.deleteById(playerUUID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(),
                () -> toorApiClient.notifyUnlink(playerUUID));

        return true;
    }

    public String getLinkedDiscordId(UUID playerUUID) {
        DBDiscordLink link = linkCache.get(playerUUID);
        return link == null ? null : link.getDiscordUserId();
    }

    public String getLinkedDiscordUsername(UUID playerUUID) {
        String discordId = getLinkedDiscordId(playerUUID);
        if (discordId == null) return null;
        return discordUsernameCache.getOrCompute(discordId, toorApiClient::getDiscordUsername);
    }
}

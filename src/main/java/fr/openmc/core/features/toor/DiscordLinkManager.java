package fr.openmc.core.features.toor;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.toor.commands.LinkCommand;
import fr.openmc.core.features.toor.commands.UnlinkCommand;
import fr.openmc.core.features.toor.event.ConnectToDiscordEvent;
import fr.openmc.core.features.toor.models.DBDiscordLink;
import fr.openmc.core.features.toor.utils.RequestSigner;
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

    private static Dao<DBDiscordLink, UUID> discordLinksDao;
    private static final Map<UUID, DBDiscordLink> linkCache = new ConcurrentHashMap<>();

    // Map<code, (Player, expireAt, task)>
    private static final Map<String, PendingLink> pendingLinks = new ConcurrentHashMap<>();
    private static final TtlCache<String, String> discordUsernameCache = new TtlCache<>(10, TimeUnit.MINUTES);

    private static final long CODE_TTL_MS = 10 * 60 * 1000;
    private static final long POLL_INTERNAL_TICKS = 20L * 3;

    @Getter
    private static String botUrl = "http://localhost:3000";

    private record PendingLink(UUID playerUUID, long expiresAt, BukkitTask pollTask) {
    }

    @Override
    protected void init() {
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

    private static void loadConfig() {
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

    private static void loadAll() {
        try {
            for (DBDiscordLink link : discordLinksDao.queryForAll()) {
                linkCache.put(link.getPlayerUUID(), link);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isLinked(UUID playerUUID) {
        return linkCache.containsKey(playerUUID);
    }

    public static String startLink(Player player) {
        UUID playerUUID = player.getUniqueId();
        cancelPendingFor(playerUUID);

        InternalToorApiClient.LinkRequestResult result = InternalToorApiClient.requestLinkCode(playerUUID, player.getName());
        if (!result.success()) {
            return null;
        }

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

    private static void pollCode(String code, UUID playerUUID) {
        PendingLink pending = pendingLinks.get(code);
        if (pending == null) return;

        if (System.currentTimeMillis() > pending.expiresAt()) {
            cancelPendingFor(playerUUID);
            notifyPlayer(playerUUID, "feature.discord.expired", MessageType.ERROR);
            return;
        }

        InternalToorApiClient.LinkStatus status = InternalToorApiClient.checkLinkStatus(code);
        if (!status.linked()) return;

        String discordUserId = status.discordUserId();
        String discordUsername = status.discordUsername();

        confirmLink(playerUUID, status.discordUserId());
        InternalToorApiClient.consumeCode(code);
        cancelPendingFor(playerUUID);
        notifyPlayer(playerUUID, "feature.discord.success", MessageType.SUCCESS, Component.text(discordUsername));
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(),
                () -> Bukkit.getPluginManager().callEvent(new ConnectToDiscordEvent(playerUUID, discordUserId, discordUsername)));
    }

    private static void confirmLink(UUID playerUUID, String discordUserId) {
        DBDiscordLink link = new DBDiscordLink(playerUUID, discordUserId);
        linkCache.put(playerUUID, link);
        try {
            discordLinksDao.createOrUpdate(link);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void notifyPlayer(UUID playerUUID, String translationKey, MessageType type, ComponentLike... args) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                MessagesManager.sendMessage(player, TranslationManager.translation(translationKey, args), Prefix.OPENMC, type, true);
            }
        });
    }

    private static void cancelPendingFor(UUID playerUUID) {
        pendingLinks.entrySet().removeIf(entry -> {
            if (entry.getValue().playerUUID().equals(playerUUID)) {
                entry.getValue().pollTask().cancel();
                return true;
            }
            return false;
        });
    }

    public static boolean unlink(UUID playerUUID) {
        if (!linkCache.containsKey(playerUUID)) return false;

        cancelPendingFor(playerUUID);
        linkCache.remove(playerUUID);

        try {
            discordLinksDao.deleteById(playerUUID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(),
                () -> InternalToorApiClient.notifyUnlink(playerUUID));

        return true;
    }

    public static String getLinkedDiscordId(UUID playerUUID) {
        DBDiscordLink link = linkCache.get(playerUUID);
        return link == null ? null : link.getDiscordUserId();
    }

    public static String getLinkedDiscordUsername(UUID playerUUID) {
        String discordId = getLinkedDiscordId(playerUUID);
        if (discordId == null) return null;
        return discordUsernameCache.getOrCompute(discordId, InternalToorApiClient::getDiscordUsername);
    }
}

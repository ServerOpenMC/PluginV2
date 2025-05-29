package fr.openmc.core.features.discordlink;

import fr.openmc.core.utils.database.DatabaseManager;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.openmc.core.OMCPlugin;

public class DiscordLinkManager {
    @Getter
    private static DiscordLinkManager instance;
    private final OMCPlugin plugin;
    @Getter
    private final DiscordBot bot;
    private static final String TABLE_NAME = "discord_links";

    // Stocke les codes de vérification temporaires (code -> uuid du joueur)
    private final Map<String, String> pendingLinks;


    public DiscordLinkManager(OMCPlugin plugin) {
        this.plugin = plugin;
        String token = plugin.getConfig().getString("bot-discord.bot-token");
        if (token != null && !token.isEmpty()) {
            this.bot = new DiscordBot(plugin);
            this.bot.startBot();
        } else {
            this.bot = null;
            plugin.getLogger().warning("Le token du bot Discord n'est pas configuré!");
        }

        instance = this;
        this.pendingLinks = new HashMap<>();
        initDatabase();
    }

    /**
     * Initializes the database table for storing Discord-Minecraft account links
     * Creates the table if it doesn't exist with columns for Minecraft UUID, Discord ID, and link date
     */
    private void initDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "minecraft_uuid VARCHAR(36) PRIMARY KEY," +
                    "discord_id VARCHAR(20) NOT NULL UNIQUE," +
                    "link_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ")").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a verification code for linking Discord and Minecraft accounts
     * @param player The Minecraft player to generate code for
     * @return The 6-digit verification code as a string
     */
    public String generateVerificationCode(Player player) {
        String code = String.format("%06d", new Random().nextInt(999999));
        pendingLinks.put(code, player.getUniqueId().toString());

        // Supprimer le code après 5 minutes
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            pendingLinks.remove(code);
        }, 20 * 60 * 5);

        return code;
    }

    /**
     * Links a Minecraft account to a Discord account using a verification code
     * @param code The verification code generated for the player
     * @param discordId The Discord ID to link with the Minecraft account
     * @return CompletableFuture that resolves to true if linking succeeded, false otherwise
     */
    public CompletableFuture<Boolean> linkAccounts(String code, String discordId) {
        return CompletableFuture.supplyAsync(() -> {
            if (!pendingLinks.containsKey(code)) {
                return false;
            }

            String minecraftUUID = pendingLinks.get(code);

            try (Connection conn = DatabaseManager.getConnection()) {
                // Vérifier si le compte Discord est déjà lié
                PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT minecraft_uuid FROM " + TABLE_NAME + " WHERE discord_id = ?");
                checkStmt.setString(1, discordId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return false; // Compte Discord déjà lié
                }

                // Insérer la nouvelle liaison
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO " + TABLE_NAME + " (minecraft_uuid, discord_id) VALUES (?, ?)");
                insertStmt.setString(1, minecraftUUID);
                insertStmt.setString(2, discordId);
                insertStmt.executeUpdate();

                pendingLinks.remove(code);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    /**
     * Retrieves the Discord ID associated with a Minecraft UUID
     * @param minecraftUUID The Minecraft player's UUID
     * @return CompletableFuture that resolves to the Discord ID or null if not linked
     */
    public CompletableFuture<String> getDiscordId(UUID minecraftUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT discord_id FROM " + TABLE_NAME + " WHERE minecraft_uuid = ?");
                stmt.setString(1, minecraftUUID.toString());
                ResultSet rs = stmt.executeQuery();

                return rs.next() ? rs.getString("discord_id") : null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Retrieves the Minecraft UUID associated with a Discord ID
     * @param discordId The Discord user's ID
     * @return CompletableFuture that resolves to the Minecraft UUID or null if not linked
     */
    public CompletableFuture<String> getMinecraftUUID(String discordId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT minecraft_uuid FROM " + TABLE_NAME + " WHERE discord_id = ?");
                stmt.setString(1, discordId);
                ResultSet rs = stmt.executeQuery();

                return rs.next() ? rs.getString("minecraft_uuid") : null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Checks if a Minecraft account is linked to any Discord account
     * @param minecraftUUID The Minecraft player's UUID to check
     * @return CompletableFuture that resolves to true if linked, false otherwise
     */
    public CompletableFuture<Boolean> isLinked(UUID minecraftUUID) {
        return getDiscordId(minecraftUUID).thenApply(Objects::nonNull);
    }

    /**
     * Unlinks a Minecraft account from its associated Discord account
     * @param minecraftUUID The Minecraft player's UUID to unlink
     * @return CompletableFuture that resolves to true if unlinking succeeded, false otherwise
     */
    public CompletableFuture<Boolean> unlinkAccount(UUID minecraftUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM " + TABLE_NAME + " WHERE minecraft_uuid = ?");
                stmt.setString(1, minecraftUUID.toString());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
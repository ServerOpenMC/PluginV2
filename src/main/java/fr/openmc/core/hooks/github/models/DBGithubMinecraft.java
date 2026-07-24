package fr.openmc.core.hooks.github.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.cache.PlayerNameCache;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@DatabaseTable(tableName = "dream_player")
@Getter
public class DBGithubMinecraft {
    @DatabaseField(id = true, columnName = "mc_uuid")
    private UUID playerUUID;

    @Setter
    @DatabaseField(canBeNull = false, columnName = "github_id")
    private int githubID;

    DBGithubMinecraft() {
        // Default constructor for ORMLite
    }

    public DBGithubMinecraft(UUID playerUUID, int githubID) {
        this.playerUUID = playerUUID;
        this.githubID = githubID;
    }

    public DBGithubMinecraft(OfflinePlayer player, int githubID) {
        this.playerUUID = player.getUniqueId();
        this.githubID = githubID;
    }

    public OfflinePlayer getPlayer() {
        return CacheOfflinePlayer.getOfflinePlayer(playerUUID);
    }

    public String getPlayerName() {
        return PlayerNameCache.getName(playerUUID);
    }
}
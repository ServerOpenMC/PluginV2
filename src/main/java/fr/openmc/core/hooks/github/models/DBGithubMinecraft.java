package fr.openmc.core.hooks.github.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.cache.CachePlayerName;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@DatabaseTable(tableName = "github_mc")
@Getter
public class DBGithubMinecraft {
    @DatabaseField(id = true, columnName = "player_uuid")
    private UUID playerUUID;

    @Setter
    @DatabaseField(canBeNull = false, columnName = "github_id")
    private long githubID;

    DBGithubMinecraft() {
        // Default constructor for ORMLite
    }

    public DBGithubMinecraft(UUID playerUUID, long githubID) {
        this.playerUUID = playerUUID;
        this.githubID = githubID;
    }

    public DBGithubMinecraft(OfflinePlayer player, long githubID) {
        this.playerUUID = player.getUniqueId();
        this.githubID = githubID;
    }

    public OfflinePlayer getPlayer() {
        return CacheOfflinePlayer.getOfflinePlayer(playerUUID);
    }

    public String getPlayerName() {
        return CachePlayerName.getName(playerUUID);
    }
}
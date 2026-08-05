package fr.openmc.core.features.discord.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "discord_link")
public class DBDiscordLink {

    @DatabaseField(id = true, columnName = "player_uuid")
    private UUID playerUUID;

    @DatabaseField(canBeNull = false, columnName = "discord_id")
    private String discordUserId;

    @DatabaseField(canBeNull = false, columnName = "linked_at")
    private long linkedAt;

    DBDiscordLink() {
        // Required for ORMLite
    }

    public DBDiscordLink(UUID playerUUID, String discordUserId) {
        this.playerUUID = playerUUID;
        this.discordUserId = discordUserId;
        this.linkedAt = System.currentTimeMillis();
    }
}

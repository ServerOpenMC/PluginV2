package fr.openmc.core.features.friend;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "friends")
public class Friend {
    @DatabaseField(canBeNull = false)
    private UUID first;
    @DatabaseField(canBeNull = false)
    private UUID second;
    @Getter
    @DatabaseField(canBeNull = false)
    private Timestamp date;
    @Getter
    @Setter
    @DatabaseField(columnName = "best_friend")
    private boolean bestFriend;

    Friend() {
        // required for ORMLite
    }

    Friend(UUID first, UUID second, Timestamp time) {
        this.first = first;
        this.second = second;
        this.date = time;
    }

    public UUID getOther(UUID player) {
        return player.equals(first) ? second : first;
    }
}

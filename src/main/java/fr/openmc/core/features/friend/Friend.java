package fr.openmc.core.features.friend;

import java.sql.Date;
import java.util.UUID;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

@DatabaseTable(tableName = "friends")
public class Friend {
    @DatabaseField(canBeNull = false)
    private UUID first;
    @DatabaseField(canBeNull = false)
    private UUID second;
    @Getter
    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Date date;
    @Setter
    @DatabaseField(columnName = "best_friend")
    private boolean bestFriend;

    Friend() {
        // required for ORMLite
    }

    Friend(UUID first, UUID second) {
        this.first = first;
        this.second = second;
    }

    public boolean isBestFriend() {
        return bestFriend;
    }

    public UUID getOther(UUID player) {
        return player == first ? second : first;
    }
}

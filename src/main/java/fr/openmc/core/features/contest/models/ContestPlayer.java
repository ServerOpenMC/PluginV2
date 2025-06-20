package fr.openmc.core.features.contest.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.utils.CacheOfflinePlayer;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.UUID;

@DatabaseTable(tableName = "contest_players")
public class ContestPlayer {
    @Getter
    @DatabaseField(id = true, columnName = "uuid")
    private UUID UUID;
    @Getter
    @Setter
    @DatabaseField(canBeNull = false)
    private int points;
    @Getter
    @DatabaseField(canBeNull = false)
    private int camp;
    @DatabaseField(canBeNull = false)
    private int color;

    ContestPlayer() {
        // required for ORMLite
    }

    public ContestPlayer(UUID uuid, int points, int camp, NamedTextColor color) {
        this.UUID = uuid;
        this.points = points;
        this.camp = camp;
        this.color = color.value();
    }

    public NamedTextColor getColor() {
        return NamedTextColor.namedColor(color);
    }

    public String getName() {
        return CacheOfflinePlayer.getOfflinePlayer(UUID).getName();
    }
}

package fr.openmc.core.features.contest.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
@DatabaseTable(tableName = "contest_player_data")
public class ContestPlayer {
    @DatabaseField(id = true)
    private String name;
    @DatabaseField(canBeNull = false)
    private int points;
    @DatabaseField(canBeNull = false)
    private int camp;
    @DatabaseField(canBeNull = false)
    private NamedTextColor color;

    ContestPlayer() {
        // required for ORMLite
    }

    public ContestPlayer(String name, int points, int camp, NamedTextColor color) {
        this.name = name;
        this.points = points;
        this.camp = camp;
        this.color = color;
    }
}

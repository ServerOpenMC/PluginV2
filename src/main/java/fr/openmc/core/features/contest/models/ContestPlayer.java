package fr.openmc.core.features.contest.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;

@DatabaseTable(tableName = "contest_players")
public class ContestPlayer {
    @Getter
    @DatabaseField(id = true)
    private String name;
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

    public ContestPlayer(String name, int points, int camp, NamedTextColor color) {
        this.name = name;
        this.points = points;
        this.camp = camp;
        this.color = color.value();
    }

    public NamedTextColor getColor() {
        return NamedTextColor.namedColor(color);
    }
}

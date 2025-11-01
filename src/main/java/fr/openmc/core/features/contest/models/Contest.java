package fr.openmc.core.features.contest.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
@DatabaseTable(tableName = "contests")
public class Contest {
    @DatabaseField(id = true)
    private int id; // required for Dao.update function

    @DatabaseField(canBeNull = false)
    private String camp1;
    @DatabaseField(canBeNull = false)
    private String camp2;
    @DatabaseField(canBeNull = false)
    private String color1;
    @DatabaseField(canBeNull = false)
    private String color2;
    @Setter
    @DatabaseField(canBeNull = false)
    private int phase;
    @DatabaseField(canBeNull = false)
    private String startdate;
    @Setter
    @DatabaseField(canBeNull = false)
    private int points1;
    @Setter
    @DatabaseField(canBeNull = false)
    private int points2;

    Contest() {
        // required for ORMLite
    }

    public Contest(String camp1, String camp2, String color1, String color2, int phase, String startdate, int points1,
            int points2) {
        this.id = 1; // we will only be storing one row, so we need a constant id
        this.camp1 = camp1;
        this.camp2 = camp2;
        this.color1 = color1;
        this.color2 = color2;
        this.phase = phase;
        this.startdate = startdate;
        this.points1 = points1;
        this.points2 = points2;
    }

    public String get(String input) {
        return switch (input) {
            case "camp1" -> camp1;
            case "camp2" -> camp2;
            case "color1" -> color1;
            case "color2" -> color2;
            case null, default -> null;
        };
    }

    public int getInteger(String input) {
        if (Objects.equals(input, "points1")) {
            return points1;
        } else if (Objects.equals(input, "points2")) {
            return points2;
        } else {
            return -1;
        }
    }

    public NamedTextColor getColor1AsNamedTextColor() {
        return NamedTextColor.NAMES.value(color1.toLowerCase());
    }

    public NamedTextColor getColor2AsNamedTextColor() {
        return NamedTextColor.NAMES.value(color2.toLowerCase());
    }

    public Component getCampVSComponent() {
        return Component.text()
                .append(Component.text(camp1, getColor1AsNamedTextColor()))
                .append(Component.text(" VS ", NamedTextColor.GRAY))
                .append(Component.text(camp2, getColor2AsNamedTextColor()))
                .build();
    }
}

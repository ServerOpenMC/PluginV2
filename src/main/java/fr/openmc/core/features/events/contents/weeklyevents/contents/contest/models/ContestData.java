package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Objects;

@Getter
@DatabaseTable(tableName = "contests")
public class ContestData {
    @DatabaseField(id = true)
    private int id; // required for Dao.update function

    @DatabaseField(canBeNull = false)
    private String keyCamp1;
    @DatabaseField(canBeNull = false)
    private String keyCamp2;
    @DatabaseField(canBeNull = false)
    private String color1;
    @DatabaseField(canBeNull = false)
    private String color2;
    @Setter
    @DatabaseField(canBeNull = false)
    private int points1;
    @Setter
    @DatabaseField(canBeNull = false)
    private int points2;

    ContestData() {
        // required for ORMLite
    }

    public ContestData(String keyCamp1, String keyCamp2, String color1, String color2, int points1, int points2) {
        this.id = 1; // we will only be storing one row, so we need a constant id
        this.keyCamp1 = keyCamp1;
        this.keyCamp2 = keyCamp2;

        this.color1 = color1;
        this.color2 = color2;
        this.points1 = points1;
        this.points2 = points2;
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
                .append(getCamp1())
                .append(Component.text(" VS ", NamedTextColor.GRAY))
                .append(getCamp2())
                .build();
    }

    public Component getCampComponent(Integer campInt) {
        if (campInt == 1) {
            return getCamp1();
        } else if (campInt == 2) {
            return getCamp2();
        } else {
            return Component.empty();
        }
    }

    public Component getCamp1() {
        return TranslationManager.translation(keyCamp1)
                .decoration(TextDecoration.ITALIC, false)
                .color(getColor1AsNamedTextColor());
    }

    public Component getCamp2() {
        return TranslationManager.translation(keyCamp2)
                .decoration(TextDecoration.ITALIC, false)
                .color(getColor2AsNamedTextColor());
    }

    public Component getCamp1ToSmall() {
        return TranslationManager.translation(keyCamp1 + ".to_small")
                .decoration(TextDecoration.ITALIC, false)
                .color(getColor1AsNamedTextColor());
    }

    public Component getCamp2ToSmall() {
        return TranslationManager.translation(keyCamp2  + ".to_small")
                .decoration(TextDecoration.ITALIC, false)
                .color(getColor2AsNamedTextColor());
    }
}

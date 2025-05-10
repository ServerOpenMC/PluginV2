package fr.openmc.core.features.contest;

import fr.openmc.core.features.contest.managers.ContestManager;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public class ContestData {
    private String camp1;
    private String camp2;
    private String color1;
    private String color2;
    @Setter
    private int phase;
    private String startdate;
    @Setter
    private int point1;
    @Setter
    private int point2;

    public ContestData(String camp1, String camp2, String color1, String color2, int phase, String startdate,
            int point1, int point2) {
        this.camp1 = camp1;
        this.camp2 = camp2;
        this.color1 = color1;
        this.color2 = color2;
        this.phase = phase;
        this.startdate = startdate;
        this.point1 = point1;
        this.point2 = point2;
    }

    public String get(String input) {
        return switch (input) {
            case "camp1" -> getCamp1();
            case "camp2" -> getCamp2();
            case "color1" -> getColor1();
            case "color2" -> getColor2();
            case null, default -> null;
        };
    }

    public int getInteger(String input) {
        if (Objects.equals(input, "points1")) {
            return getPoint1();
        } else if (Objects.equals(input, "points2")) {
            return getPoint2();
        } else {
            return -1;
        }
    }
}

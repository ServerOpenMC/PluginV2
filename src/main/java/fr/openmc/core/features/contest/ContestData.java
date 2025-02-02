package fr.openmc.core.features.contest;

import fr.openmc.core.features.contest.managers.ContestManager;
import lombok.Getter;

import java.util.Objects;

@Getter
public class ContestData {
    private final String camp1;
    private final String camp2;
    private final String color1;
    private final String color2;
    private final int phase;
    private final String startdate;
    private final int point1;
    private final int point2;

    public ContestData(String camp1, String camp2, String color1, String color2, int phase, String startdate, int point1, int point2) {
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

    public void setPhase(int phase) {
        ContestData data = ContestManager.getInstance().data;
        ContestManager.getInstance().data = new ContestData(data.getCamp1(), data.getCamp2(), data.getColor1(), data.getColor2(), phase, data.getStartdate(), data.getPoint1(), data.getPoint2());
    }

    public void setPointsCamp1(int points) {
        ContestData data = ContestManager.getInstance().data;
        ContestManager.getInstance().data = new ContestData(data.getCamp1(), data.getCamp2(), data.getColor1(), data.getColor2(), data.getPhase(), data.getStartdate(), points, data.getPoint2());
    }

    public void setPointsCamp2(int points) {
        ContestData data = ContestManager.getInstance().data;
        ContestManager.getInstance().data = new ContestData(data.getCamp1(), data.getCamp2(), data.getColor1(), data.getColor2(), data.getPhase(), data.getStartdate(), data.getPoint1(), points);
    }
}

package fr.openmc.core.features.contest;
import org.bukkit.ChatColor;

public class ContestPlayer {
    private final String name;
    private final int points;
    private final int camp;
    private final ChatColor color;

    public ContestPlayer(String name, int points, int camp, ChatColor color) {
        this.name = name;
        this.points = points;
        this.camp = camp;
        this.color = color;
    }

    public String getName() { return name; }

    public int getPoints() {
        return points;
    }

    public int getCamp() {
        return camp;
    }

    public ChatColor getColor() {
        return color;
    }
}


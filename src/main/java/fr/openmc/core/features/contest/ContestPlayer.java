package fr.openmc.core.features.contest;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public class ContestPlayer {
    private final String name;
    private final int points;
    private final int camp;
    private final NamedTextColor color;

    public ContestPlayer(String name, int points, int camp, NamedTextColor color) {
        this.name = name;
        this.points = points;
        this.camp = camp;
        this.color = color;
    }
}


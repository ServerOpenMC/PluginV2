package fr.openmc.core.features.leaderboards;

import fr.openmc.core.utils.world.entities.TextDisplay;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public abstract class LeaderBoard {

    public String id;

    @Getter
    private Location location;

    private TextDisplay display;

    public LeaderBoard(String id, Location location, TextDisplay display) {
        this.id = id;
        this.location = location;
        this.display = display;
    }

    public abstract void update();

}

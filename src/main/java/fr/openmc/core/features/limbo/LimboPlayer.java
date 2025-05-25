package fr.openmc.core.features.limbo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LimboPlayer {
    @Getter
    private final Player bukkitPlayer;
    @Getter
    private final UUID playerUUID;
    @Getter
    @Setter
    private Location virtualLocation;

    public LimboPlayer(Player player, Location initialVirtualLocation) {
        this.bukkitPlayer = player;
        this.playerUUID = player.getUniqueId();
        this.virtualLocation = initialVirtualLocation;
    }

    public void updateVirtualLocation(double x, double y, double z) {
        setVirtualLocation(new Location(null, x, y, z));
    }

}

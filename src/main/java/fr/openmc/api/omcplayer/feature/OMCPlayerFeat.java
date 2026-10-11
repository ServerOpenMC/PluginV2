package fr.openmc.api.omcplayer.feature;

import fr.openmc.api.omcplayer.OMCPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OMCPlayerFeat implements OMCFeat {

    @Getter
    private final Player player;

    public OMCPlayerFeat(Player player) {
        this.player = player;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public OMCPlayer getPlayer() {
        return OMCPlayer.of(player);
    }
}

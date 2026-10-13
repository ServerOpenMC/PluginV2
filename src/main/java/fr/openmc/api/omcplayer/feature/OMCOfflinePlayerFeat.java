package fr.openmc.api.omcplayer.feature;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OMCOfflinePlayerFeat implements OMCFeat {

    @Getter
    private final OfflinePlayer offlinePlayer;

    public OMCOfflinePlayerFeat(OfflinePlayer player) {
        this.offlinePlayer = player;
    }

    public UUID getUniqueId() {
        return offlinePlayer.getUniqueId();
    }

    public OMCOfflinePlayer getOMCOfflinePlayer() {
        return OMCOfflinePlayer.of(offlinePlayer);
    }

    @Nullable
    public OMCPlayer getPlayer() {
        return OMCPlayer.of(offlinePlayer instanceof Player online ? online : offlinePlayer.getPlayer());
    }
}

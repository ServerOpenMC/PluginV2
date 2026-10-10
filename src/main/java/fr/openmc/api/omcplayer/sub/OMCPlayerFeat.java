package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.omcplayer.OMCPlayer;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OMCPlayerFeat {

    @Getter
    private final OfflinePlayer offlinePlayer;

    public OMCPlayerFeat(OfflinePlayer player) {
        this.offlinePlayer = player;
    }

    public UUID getUniqueId() {
        return offlinePlayer.getUniqueId();
    }

    @Nullable
    public OMCPlayer getPlayer() {
        return OMCPlayer.of(offlinePlayer instanceof Player online ? online : offlinePlayer.getPlayer());
    }
}

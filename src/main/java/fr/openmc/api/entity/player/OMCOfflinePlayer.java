package fr.openmc.api.entity.player;

import fr.openmc.api.entity.player.sub.OMCPlayerHome;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "removal", "deprecation"})
public interface OMCOfflinePlayer extends OfflinePlayer {

    static OMCOfflinePlayer of(@NotNull OfflinePlayer player) {
        if (player instanceof Player online)
            return OMCPlayer.of(online);
        if (player instanceof OMCOfflinePlayer omcPlayer)
            return omcPlayer;
        return new OMCOfflinePlayerImpl(player);
    }

    @NotNull OfflinePlayer getOfflinePlayer();

    OMCPlayerHome home();
}

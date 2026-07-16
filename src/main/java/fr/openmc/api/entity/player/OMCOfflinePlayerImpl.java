package fr.openmc.api.entity.player;

import fr.openmc.api.entity.player.sub.OMCPlayerHome;
import lombok.experimental.Delegate;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "removal", "deprecation"})
public class OMCOfflinePlayerImpl implements OMCOfflinePlayer {

    @Delegate(types = OfflinePlayer.class)
    private final OfflinePlayer player;
    private final OMCPlayerHome home;

    OMCOfflinePlayerImpl(OfflinePlayer player) {
        this.player = player;
        this.home = new OMCPlayerHome(player);
    }

    @Override
    public @NotNull OfflinePlayer getOfflinePlayer() {
        return player;
    }

    @Override
    public OMCPlayerHome home() {
        return home;
    }
}

package fr.openmc.api.omcplayer;

import fr.openmc.api.omcplayer.sub.OMCPlayerHome;
import fr.openmc.core.utils.cache.CachePlayerName;
import lombok.experimental.Delegate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;
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
    public Component getNameWithHead() {
        if (player == null)
            return CachePlayerName.name(null);

        String playerName = player.getName();

        if (playerName == null)
            playerName = CachePlayerName.getName(player.getUniqueId());

        return Component.object(ObjectContents.playerHead(player.getUniqueId())).color(NamedTextColor.WHITE)
                .appendSpace()
                .append(Component.text(playerName));
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

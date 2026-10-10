package fr.openmc.api.omcplayer;

import fr.openmc.api.omcplayer.sub.*;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings({"unused", "removal", "deprecation"})
public interface OMCOfflinePlayer extends OfflinePlayer {
    static OMCOfflinePlayer of(UUID playerUUID) {
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);
        return new OMCOfflinePlayerImpl(offlinePlayer);
    }

    static OMCOfflinePlayer of(String playerName) {
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerName);
        return new OMCOfflinePlayerImpl(offlinePlayer);
    }

    static OMCOfflinePlayer of(@NotNull OfflinePlayer player) {
        if (player instanceof Player online)
            return OMCPlayer.of(online);
        if (player instanceof OMCOfflinePlayer omcPlayer)
            return omcPlayer;
        return new OMCOfflinePlayerImpl(player);
    }

    @NotNull OfflinePlayer getOfflinePlayer();

    Component getNameWithHead();

    OMCPlayerMessage message();
    OMCPlayerHome home();
    OMCPlayerEconomy economy();
    OMCPlayerCity city();
    OMCPlayerCooldown cooldown();
}

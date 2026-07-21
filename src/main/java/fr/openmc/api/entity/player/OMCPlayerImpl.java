package fr.openmc.api.entity.player;

import fr.openmc.api.entity.player.sub.OMCPlayerCity;
import fr.openmc.api.entity.player.sub.OMCPlayerEconomy;
import fr.openmc.api.entity.player.sub.OMCPlayerMessage;
import fr.openmc.api.entity.player.sub.OMCPlayerSettings;
import lombok.experimental.Delegate;
import org.bukkit.entity.Player;

@SuppressWarnings({"unused", "removal", "deprecation"})
public class OMCPlayerImpl extends OMCOfflinePlayerImpl implements OMCPlayer {

    @Delegate(types = Player.class)
    private final Player player;
    private final OMCPlayerMessage message;
    private final OMCPlayerCity city;
    private final OMCPlayerEconomy economy;
    private final OMCPlayerSettings settings;

    private OMCPlayerImpl(Player player) {
        super(player);
        this.player = player;
        this.message = new OMCPlayerMessage(player);
        this.city = new OMCPlayerCity(player);
        this.economy = new OMCPlayerEconomy(player);
        this.settings = new OMCPlayerSettings(player);
    }

    static OMCPlayer of(Player player) {
        if (player instanceof OMCPlayer omcPlayer)
            return omcPlayer;
        return new OMCPlayerImpl(player);
    }

    @Override
    public OMCPlayerMessage message() {
        return message;
    }

    @Override
    public OMCPlayerEconomy economy() {
        return economy;
    }

    @Override
    public OMCPlayerCity city() {
        return city;
    }

    @Override
    public OMCPlayerSettings settings() {
        return settings;
    }
}

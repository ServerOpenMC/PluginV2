package fr.openmc.api.omcplayer;

import fr.openmc.api.omcplayer.sub.*;
import lombok.experimental.Delegate;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unused", "removal", "deprecation"})
public class OMCPlayerImpl extends OMCOfflinePlayerImpl implements OMCPlayer {
    private static final Map<UUID, OMCPlayer> CACHE = new ConcurrentHashMap<>();

    @Delegate(types = Player.class)
    private final Player player;
    private final OMCPlayerSettings settings;
    private final OMCPlayerChronometer chronometer;
    private final OMCPlayerInputs intpus;
    private final OMCPlayerCorpse corpse;

    private OMCPlayerImpl(Player player) {
        super(player);
        this.player = player;
        this.settings = new OMCPlayerSettings(player);
        this.chronometer = new OMCPlayerChronometer(player);
        this.intpus = new OMCPlayerInputs(player);
        this.corpse = new OMCPlayerCorpse(player);
    }

    static OMCPlayer of(Player player) {
        if (player == null) throw new IllegalArgumentException("player ne peut pas être null");

        return CACHE.compute(player.getUniqueId(), (id, cachedPlayer) -> {
            if (cachedPlayer == null) return new OMCPlayerImpl(player);

            Player cachedBukkitPlayer = cachedPlayer.getPlayer();

            if (cachedBukkitPlayer == null
                    || !cachedBukkitPlayer.isOnline()
                    || cachedBukkitPlayer != player)
                return new OMCPlayerImpl(player);


            return cachedPlayer;
        });
    }

    static OMCPlayer of(UUID playerUUID) {
        Player playerBukkit = Bukkit.getPlayer(playerUUID);

        if (playerBukkit == null) throw new IllegalArgumentException("player ne peut pas être null");

        return of(playerBukkit);
    }

    @Override
    public @Nullable Player getPlayer() {
        return player;
    }

    @Override
    public @Nullable CraftPlayer getCraftPlayer() {
        return (CraftPlayer) player;
    }

    @Override
    public ServerPlayer getServerPlayer() {
        if (getCraftPlayer() == null)
            throw new IllegalStateException("getCraftPlayer() ne devrait pas être null");
        return getCraftPlayer().getHandle();
    }

    public static OMCPlayer getCache(UUID uuid) {
        return CACHE.get(uuid);
    }

    public static void removeCache(UUID uuid) {
        CACHE.remove(uuid);
    }

    @Override
    public OMCPlayerSettings settings() {
        return settings;
    }

    @Override
    public OMCPlayerChronometer chronometer() {
        return chronometer;
    }

    @Override
    public OMCPlayerInputs inputs() {
        return intpus;
    }

    @Override
    public OMCPlayerCorpse corpse() {
        return corpse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player p)) return false;
        return player.getUniqueId().equals(p.getUniqueId());
    }

    @Override
    public int hashCode() {
        return player.getUniqueId().hashCode();
    }
}

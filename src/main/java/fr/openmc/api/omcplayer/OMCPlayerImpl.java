package fr.openmc.api.omcplayer;

import fr.openmc.api.omcplayer.sub.OMCPlayerCity;
import fr.openmc.api.omcplayer.sub.OMCPlayerEconomy;
import fr.openmc.api.omcplayer.sub.OMCPlayerMessage;
import fr.openmc.api.omcplayer.sub.OMCPlayerSettings;
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
        if (player == null) throw new IllegalArgumentException("player ne peut pas être null");


        return CACHE.compute(player.getUniqueId(), (id, cachedPlayer) -> {
            if (cachedPlayer == null) return new OMCPlayerImpl(player);

            Player cachedBukkitPlayer = cachedPlayer.getPlayer();

            if (cachedBukkitPlayer == null) return new OMCPlayerImpl(player);

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

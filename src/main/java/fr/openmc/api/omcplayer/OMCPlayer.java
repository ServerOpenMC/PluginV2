package fr.openmc.api.omcplayer;

import fr.openmc.api.omcplayer.sub.OMCPlayerCity;
import fr.openmc.api.omcplayer.sub.OMCPlayerEconomy;
import fr.openmc.api.omcplayer.sub.OMCPlayerMessage;
import fr.openmc.api.omcplayer.sub.OMCPlayerSettings;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Wrapper autour d'un {@link Player} pour les methodes propres a OpenMC
 * (economie, ville, menus...).
 * <p>
 * Exemples :
 * <pre>{@code
 * // Dans une commande Lamp, en sender ou en argument :
 * @Command("balance")
 * public void balance(OMCPlayer player) {
 *      player.message().sendInfo(Component.text("Vous avez " + player.getFormattedBalance()));
 * }
 *
 * // Dans un event :
 * OMCPlayer player = OMCPlayer.of(event.getPlayer());
 * if (player.hasCity()) { ... }
 * }</pre>
 */
@SuppressWarnings({"unused", "removal", "deprecation"})
public interface OMCPlayer extends OMCOfflinePlayer, Player {

    static OMCPlayer of(@Nullable Player player) {
        return OMCPlayerImpl.of(player);
    }

    static OMCPlayer of(UUID playerUUID) {
        return OMCPlayerImpl.of(playerUUID);
    }

    @Nullable Player getPlayer();
    @Nullable CraftPlayer getCraftPlayer();
    ServerPlayer getServerPlayer();

    OMCPlayerMessage message();

    OMCPlayerEconomy economy();

    OMCPlayerCity city();

    OMCPlayerSettings settings();

}

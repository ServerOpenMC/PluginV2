package fr.openmc.api.entity.player;

import fr.openmc.api.entity.player.sub.OMCPlayerCity;
import fr.openmc.api.entity.player.sub.OMCPlayerEconomy;
import fr.openmc.api.entity.player.sub.OMCPlayerMessage;
import fr.openmc.api.entity.player.sub.OMCPlayerSettings;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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

    @Nullable Player getPlayer();

    OMCPlayerMessage message();

    OMCPlayerEconomy economy();

    OMCPlayerCity city();

    OMCPlayerSettings settings();

}

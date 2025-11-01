package fr.openmc.core.features.displays.scoreboards;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.api.scoreboard.SternalBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.*;

import static fr.openmc.core.utils.messages.MessagesManager.textToSmall;

public abstract class BaseScoreboard {
    protected static final boolean canShowLogo = ItemsAdderHook.isHasItemAdder();

    /**
     * Initialise le scoreboard pour un joueur
     *
     * @param player Le joueur
     * @param board Le scoreboard à initialiser
     */
    public void init(Player player, SternalBoard board) {
        updateTitle(board);
        update(player, board);
    }

    /**
     * Met à jour le titre du scoreboard
     *
     * @param board Le scoreboard à mettre à jour
     */
    protected void updateTitle(SternalBoard board) {
        board.updateTitle(getTitle());
    }

    public Component getTitle() {
        return canShowLogo
                ? Component.text(FontImageWrapper.replaceFontImages(":openmc:"))
                : Component.text("OPENMC", NamedTextColor.LIGHT_PURPLE);
    }

    /**
     * Met à jour les lignes du scoreboard
     *
     * @param player Le joueur
     * @param board Le scoreboard à mettre à jour
     */
    protected abstract void update(Player player, SternalBoard board);

    /**
     * Détermine si le scoreboard doit être affiché pour un joueur
     *
     * @param player Le joueur à vérifier
     * @return true si le scoreboard doit être affiché, false sinon
     */
    protected abstract boolean shouldDisplay(Player player);

    /**
     * @return La priorité du scoreboard (plus la valeur est haute, plus la priorité est élevée).
     */
    protected abstract int priority();

    /**
     * @return L'intervalle de mise à jour en secondes
     */
    protected int updateInterval() {
        return 5; // Toutes les 5 secondes par défaut
    }

    /**
     * @return Un {@link Component} pour le footer
     */
    protected Component getFooter() {
        return MiniMessage.miniMessage().deserialize("     <gradient:#FF18DD:#FF80F6>%s</gradient>".formatted(textToSmall("play.openmc.fr")));
    }
}
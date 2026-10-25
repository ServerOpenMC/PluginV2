package fr.openmc.core.features.displays.scoreboards;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.core.features.displays.scoreboards.sb.InfoSwapper;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.features.settings.values.SwappingValues;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class BaseScoreboard {
    protected static final boolean canShowLogo = ItemsAdderHook.isEnable();

    private int currentSwappingTime = 200; // Pour éviter d'attendre XXs au début
    private int currentSwappingInfoIndex = 0;
    InfoSwapper currentInfo = null;

    /**
     * Initialise le scoreboard pour un joueur
     *
     * @param player Le joueur
     * @param board Le scoreboard à initialiser
     */
    public void init(Player player, SternalBoard board) {
        updateTitle(player, board);
        update(player, board);
    }

    /**
     * Met à jour le titre du scoreboard
     *
     * @param player Le joueur du scoreboard à mettre à jour
     * @param board Le scoreboard à mettre à jour
     */
    protected abstract void updateTitle(Player player, SternalBoard board);

    /**
     * Met à jour les lignes du scoreboard
     *
     * @param player Le joueur
     * @param board Le scoreboard à mettre à jour
     */
    protected abstract void update(Player player, SternalBoard board);

    /**
     * Met à jour les lignes "swap" du scoreboard
     *
     * @param player Le joueur
     * @param lines les lines dont il faut ajouter les éléments du swapping
     */
    protected void updateSwapping(Player player, List<Component> lines) {
        currentSwappingTime += updateInterval();

        int playerSwappingTime = getSwappingTime(player);

        if (playerSwappingTime == -1) {
            List<InfoSwapper> infos = getSwapping();

            if (infos.isEmpty())
                return;

            infos.forEach(info -> {
                if (info.isActive())
                    info.addInfosTo(player, lines);
            });
            return;
        }

        if (playerSwappingTime <= currentSwappingTime) {
            currentSwappingTime = 0;
            currentInfo = getNextInfo();
        }

        if (currentInfo != null)
            currentInfo.addInfosTo(player, lines);
    }

    /**
     * Récupère la liste des Infos à afficher à tour de role
     */
    protected List<InfoSwapper> getSwapping() {return List.of();}

    /**
     * Récupère la le temps de "swap" du joueur
     */
    protected int getSwappingTime(Player player) {
        PlayerSettings settings = PlayerSettingsManager.getPlayerSettings(player);

        if (settings == null)
            return SwappingValues.NORMAL.getSwappingTime();

        SwappingValues value = settings.getSetting(SettingType.SWAPPING_UPDATE_TIME);

        return value == null ? SwappingValues.NORMAL.getSwappingTime() : value.getSwappingTime();
    }

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
     * @return Un {@link Component} pour le titre
     */
    public Component getTitle() {
        return canShowLogo
                ? Component.text(FontImageWrapper.replaceFontImages(":openmc:"))
                : TranslationManager.translation("feature.displays.scoreboard.title.text").color(NamedTextColor.LIGHT_PURPLE);
    }

    /**
     * @return Un {@link Component} pour le footer
     */
    public static Component getFooter() {
        Component footerText = TranslationManager.translation("feature.displays.scoreboard.footer.text");
        return MiniMessage.miniMessage().deserialize("     <gradient:#FF18DD:#FF80F6><name></gradient>", Placeholder.component("name", footerText));
    }

    /**
     * Récupère la prochaine info à afficher dans le scoreboard
     */
    private InfoSwapper getNextInfo() {
        List<InfoSwapper> infos = getSwapping();

        if (infos.isEmpty())
            return null;

        infos = infos.stream().filter(InfoSwapper::isActive).toList();

        if (infos.size() <= 1)
            return null;

        currentSwappingInfoIndex = (currentSwappingInfoIndex + 1) % infos.size();
        return infos.get(currentSwappingInfoIndex);

    }
}

package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

/**
 * Enumération représentant les récompenses de niveaux pour la Mascotte.
 * Chaque niveau définit une limite maximale pour la Mascotte.
 */
@Getter
public enum MascotsLevelsRewards implements CityRewards {

    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4),
    LEVEL_5(5),
    LEVEL_6(6),
    LEVEL_7(7),
    LEVEL_8(8),
    LEVEL_9(9),
    LEVEL_10(10);

    /**
     * Limite de niveau de la mascotte pour ce niveau.
     */
    private final Integer mascotsLevelLimit;

    /**
     * Constructeur de l'énumération.
     *
     * @param mascotsLevelLimit la limite de niveau de la mascotte pour ce niveau
     */
    MascotsLevelsRewards(Integer mascotsLevelLimit) {
        this.mascotsLevelLimit = mascotsLevelLimit;
    }

    /**
     * Retourne la limite du niveau de mascotte.
     * La méthode retourne la valeur du niveau fourni.
     *
     * @param level le niveau souhaité
     * @return la limite du niveau
     */
    public static int getMascotsLevelLimit(int level) {
        return level;
    }

    /**
     * Retourne un composant texte décrivant le niveau maximum pour la Mascotte.
     *
     * @return un composant texte indiquant le niveau maximum pour la Mascotte
     */
    @Override
    public Component getName() {
	    return Component.text("§cNiveau " + mascotsLevelLimit + " §7maximum pour la mascotte");
    }
}

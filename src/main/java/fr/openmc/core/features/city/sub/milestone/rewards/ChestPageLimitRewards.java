package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

/**
 * Enumération représentant les récompenses de limite de pages de coffre pour une ville.
 * Chaque niveau définit une limite spécifique qui sera utilisée pour déterminer le nombre maximal de pages de coffre.
 */
@Getter
public enum ChestPageLimitRewards implements CityRewards {

    LEVEL_1(0),
    LEVEL_2(1),
    LEVEL_3(2),
    LEVEL_4(3),
    LEVEL_5(4),
    LEVEL_6(5),
    LEVEL_7(6),
    LEVEL_8(8),
    LEVEL_9(9),
    LEVEL_10(10);

    /**
     * Limite de pages de coffre associée à ce niveau.
     */
    private final Integer chestPageLimit;

    /**
     * Constructeur de l'énumération.
     *
     * @param chestPageLimit la limite de pages de coffre pour ce niveau.
     */
    ChestPageLimitRewards(Integer chestPageLimit) {
        this.chestPageLimit = chestPageLimit;
    }

    /**
     * Retourne la limite de pages de coffre correspondant au niveau donné.
     * Si la limite du niveau est nulle, une recherche dans les niveaux inférieurs est effectuée.
     *
     * @param level le niveau pour lequel obtenir la limite de pages de coffre.
     * @return la limite correspondante.
     * @throws IllegalArgumentException si le niveau est invalide.
     */
    public static int getChestPageLimit(int level) {
        ChestPageLimitRewards[] values = ChestPageLimitRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
        }

        ChestPageLimitRewards reward = values[level - 1];
        if (reward.chestPageLimit != null) {
            return reward.chestPageLimit;
        }

        for (int i = level - 2; i >= 0; i--) {
            if (values[i].chestPageLimit != null) {
                return values[i].chestPageLimit;
            }
        }

        return 0;
    }

    /**
     * Retourne le nom de la récompense sous forme d'un composant texte.
     * Le texte indique la limite de pages de coffre maximum pour ce niveau.
     *
     * @return un composant texte décrivant la récompense.
     */
    @Override
    public Component getName() {
        return Component.text("§a" + chestPageLimit + " pages de coffre §7maximum");
    }
}

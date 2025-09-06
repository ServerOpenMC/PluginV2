package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

/**
 * Enumération représentant les récompenses d'intérêt pour une ville.
 * Chaque niveau définit un pourcentage d'intérêt spécifique.
 */
@Getter
public enum InterestRewards implements CityRewards {

    LEVEL_1(.00),
    LEVEL_2(.00),
    LEVEL_3(.00),
    LEVEL_4(.01),
    LEVEL_5(.02),
    LEVEL_6(.01),
    LEVEL_7(.01),
    LEVEL_8(.01),
    LEVEL_9(.01),
    LEVEL_10(.03);

    /**
     * Pourcentage d'intérêt pour le niveau.
     */
    private final double interest;

    /**
     * Constructeur de l'énumération.
     *
     * @param interest le pourcentage d'intérêt associé au niveau
     */
    InterestRewards(double interest) {
        this.interest = interest;
    }

    /**
     * Calcule l'intérêt total cumulé jusqu'au niveau spécifié.
     *
     * @param level le niveau jusqu'auquel cumule l'intérêt
     * @return l'intérêt total cumulé
     * @throws IllegalArgumentException si le niveau est invalide
     */
    public static double getTotalInterest(int level) {
        InterestRewards[] values = InterestRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
        }

        double total = .00;
        for (int i = 0; i < level; i++) {
            total += values[i].interest;
        }
        return total;
    }

    /**
     * Retourne le nom de la récompense sous forme d'un composant texte.
     *
     * @return un composant texte décrivant le pourcentage d'intérêt
     */
    @Override
    public Component getName() {
        return Component.text("§7+ §6" + interest * 100 + "% §6d'intérêt");
    }
}

package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

/**
 * Enumération représentant les récompenses de limite de membres pour une ville.
 * Chaque niveau définit une limite spécifique pour le nombre maximum de membres.
 */
@Getter
public enum MemberLimitRewards implements CityRewards {

    LEVEL_1(2),
    LEVEL_2(3),
    LEVEL_3(5),
    LEVEL_4(null),
    LEVEL_5(7),
    LEVEL_6(10),
    LEVEL_7(15),
    LEVEL_8(20),
    LEVEL_9(null),
    LEVEL_10(25);

    /**
     * Limite de membres pour ce niveau.
     */
    private final Integer memberLimit;

    /**
     * Constructeur de l'énumération.
     *
     * @param memberLimit la limite de membres pour le niveau
     */
    MemberLimitRewards(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    /**
     * Récupère la limite de membres pour un niveau donné.
     * Si la valeur du niveau demandé est nulle, la méthode recherche la valeur la plus proche dans les niveaux inférieurs.
     *
     * @param level le niveau de la ville
     * @return la limite de membres correspondante
     * @throws IllegalArgumentException si le niveau est invalide
     */
    public static int getMemberLimit(int level) {
        MemberLimitRewards[] values = MemberLimitRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
        }

        MemberLimitRewards reward = values[level - 1];
        if (reward.memberLimit != null) {
            return reward.memberLimit;
        }

        for (int i = level - 2; i >= 0; i--) {
            if (values[i].memberLimit != null) {
                return values[i].memberLimit;
            }
        }
        return 0;
    }

    /**
     * Retourne un composant texte décrivant la limite maximale de membres pour ce niveau.
     *
     * @return le composant texte
     */
    @Override
    public Component getName() {
        return Component.text("§d" + memberLimit + " §7membres maximum");
    }
}

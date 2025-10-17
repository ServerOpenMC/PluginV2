package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

/**
 * Énumération représentant les récompenses de limite de solde
 * dans la banque personnelle pour chaque niveau de ville.
 */
@Getter
public enum PlayerBankLimitRewards implements CityRewards {

    LEVEL_1(null),
    LEVEL_2(10000),
    LEVEL_3(15000),
    LEVEL_4(20000),
    LEVEL_5(35000),
    LEVEL_6(50000),
    LEVEL_7(75000),
    LEVEL_8(100000),
    LEVEL_9(125000),
    LEVEL_10(150000);

    /**
     * Limite de solde de la banque personnelle pour le niveau.
     */
    private final Integer bankBalanceLimit;

    /**
     * Constructeur de l'énumération.
     *
     * @param bankBalanceLimit la limite de solde de la banque personnelle
     */
    PlayerBankLimitRewards(Integer bankBalanceLimit) {
        this.bankBalanceLimit = bankBalanceLimit;
    }

    /**
     * Récupère la limite de solde correspondant à un niveau donné.
     * Si le niveau demandé n'a pas de valeur définie, on retourne
     * la limite du niveau précédent le plus proche.
     *
     * @param level le niveau de la ville
     * @return la limite de solde de la banque personnelle
     * @throws IllegalArgumentException si le niveau est invalide
     */
    public static int getBankBalanceLimit(int level) {
        PlayerBankLimitRewards[] values = PlayerBankLimitRewards.values();

        if (level < 1 || level > values.length) {
	        throw new IllegalArgumentException("Niveau invalide : " + level);
        }

        PlayerBankLimitRewards reward = values[level - 1];
        if (reward.bankBalanceLimit != null) {
            return reward.bankBalanceLimit;
        }

        for (int i = level - 2; i >= 0; i--) {
            if (values[i].bankBalanceLimit != null) {
                return values[i].bankBalanceLimit;
            }
        }

        return 0;
    }

    /**
     * Retourne un composant texte décrivant la limite de solde dans la banque personnelle.
     *
     * @return un composant texte avec la limite de solde
     */
    @Override
    public Component getName() {
	    return Component.text("§7Limite à §6" + bankBalanceLimit + " d'argent §7dans la §bbanque personnelle");
    }
}

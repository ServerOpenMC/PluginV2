package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;


/**
 * Enumération représentant les récompenses de fonctionnalités débloquées pour une ville.
 * Chaque niveau définit les fonctionnalités accessibles pour ce niveau.
 */
@Getter
public enum FeaturesRewards implements CityRewards {

    LEVEL_1(),
    LEVEL_2(Feature.CHEST, Feature.CITY_BANK, Feature.PLAYER_BANK),
    LEVEL_3(Feature.NOTATION, Feature.RANK),
    LEVEL_4(Feature.MAYOR, Feature.PERK_AGRICULTURAL),
    LEVEL_5(Feature.PERK_ECONOMY),
    LEVEL_6(),
    LEVEL_7(Feature.TYPE_WAR, Feature.WAR),
    LEVEL_8(Feature.PERK_MILITARY),
    LEVEL_9(Feature.PERK_STRATEGY),
    LEVEL_10();

    /**
     * Tableau des fonctionnalités débloquées à ce niveau.
     */
    private final Feature[] features;

    /**
     * Constructeur de l'enumération.
     *
     * @param features une liste variable de fonctionnalités débloquées
     */
    FeaturesRewards(Feature... features) {
        this.features = features;
    }

    /**
     * Vérifie si la ville a débloqué la fonctionnalité spécifiée.
     * La méthode parcourt les récompenses correspondant aux niveaux inférieurs au niveau de la ville.
     *
     * @param city    la ville dont on vérifie les fonctionnalités débloquées
     * @param feature la fonctionnalité recherchée
     * @return true si la fonctionnalité est débloquée, false sinon
     */
    public static boolean hasUnlockFeature(City city, Feature feature) {
        if (feature == null || city == null) return false;

        int cityLevel = city.getLevel();
        for (int i = 0; i < cityLevel && i < values().length; i++) {
            FeaturesRewards reward = values()[i];
            if (reward.features != null) {
                for (Feature f : reward.features) {
                    if (f == feature) return true;
                }
            }
        }
        return false;
    }

    /**
     * Retourne le niveau de déblocage de la fonctionnalité spécifiée.
     *
     * @param feature la fonctionnalité dont on recherche le niveau de déblocage
     * @return le niveau de déblocage, ou -1 si la fonctionnalité n'est pas trouvée
     */
    public static int getFeatureUnlockLevel(Feature feature) {
        if (feature == null) return -1;
        for (FeaturesRewards reward : values()) {
            if (reward.features != null) {
                for (Feature f : reward.features) {
                    if (f == feature) return reward.ordinal() + 1;
                }
            }
        }
        return -1;
    }

    /**
     * Retourne le nom de la récompense sous forme de composant texte.
     * Le nom est basé sur la ou les fonctionnalités débloquées à ce niveau.
     *
     * @return un composant texte décrivant les fonctionnalités débloquées
     */
    @Override
    public Component getName() {
        if (features == null || features.length == 0) {
            return Component.text("Aucun");
        }
        if (features.length == 1) {
            return Component.text("§7Débloque " + features[0].getName());
        }

        StringBuilder sb = new StringBuilder("§7Débloque ");
        for (int i = 0; i < features.length; i++) {
            sb.append(features[i].getName());
            if (i < features.length - 2) sb.append("§7, ");
            else if (i == features.length - 2) sb.append(" §7et ");
        }
        return Component.text(sb.toString());
    }

    /**
     * Enumération interne représentant les différentes fonctionnalités pouvant être débloquées.
     */
    @Getter
    public enum Feature {
        CHEST("§a/city chest"),
        CITY_BANK("§6/city bank"),
        PLAYER_BANK("§b/bank"),
        NOTATION("§3/city notation"),
        RANK("§6/city ranks"),
        MAYOR("§6/city mayor"),
	    PERK_AGRICULTURAL("§3les réformes d'agriculture"),
	    PERK_ECONOMY("§3les réformes d'économie"),
	    TYPE_WAR("§cle type de ville de guerre"),
        WAR("§c/war"),
	    PERK_MILITARY("§3les réformes militaires"),
	    PERK_STRATEGY("§3les réformes de stratégies");

        /**
         * Nom de la fonctionnalité.
         */
        private final String name;

        /**
         * Constructeur de la fonctionnalité.
         *
         * @param name le nom associé à la fonctionnalité
         */
        Feature(String name) {
            this.name = name;
        }
    }
}

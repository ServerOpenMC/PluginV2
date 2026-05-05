package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityRewards;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
    LEVEL_8(Feature.PERK_DREAM),
    LEVEL_9(Feature.PERK_MILITARY),
    LEVEL_10(Feature.PERK_STRATEGY);

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
            return TranslationManager.translation("feature.city.levels.rewards.none");
        }
        if (features.length == 1) {
            return TranslationManager.translation(
                    "feature.city.levels.rewards.unlock",
                    features[0].getName()
            );
        }

        return TranslationManager.translation(
                "feature.city.levels.rewards.unlock",
                buildFeatureList()
        );
    }

    private Component buildFeatureList() {
        Component separator = TranslationManager.translation("feature.city.levels.rewards.list.separator")
                .appendSpace();
        Component lastSeparator = Component.space()
                .append(TranslationManager.translation("feature.city.levels.rewards.list.last_separator"))
                .appendSpace();

        Component list = features[0].getName();
        for (int i = 1; i < features.length; i++) {
            Component currentSeparator = (i == features.length - 1) ? lastSeparator : separator;
            list = list.append(currentSeparator).append(features[i].getName());
        }
        return list;
    }

    /**
     * Enumération interne représentant les différentes fonctionnalités pouvant être débloquées.
     */
    @Getter
    public enum Feature {
        CHEST("feature.city.levels.rewards.feature.chest"),
        CITY_BANK("feature.city.levels.rewards.feature.city_bank"),
        PLAYER_BANK("feature.city.levels.rewards.feature.player_bank"),
        NOTATION("feature.city.levels.rewards.feature.notation"),
        RANK("feature.city.levels.rewards.feature.rank"),
        MAYOR("feature.city.levels.rewards.feature.mayor"),
        PERK_AGRICULTURAL("feature.city.levels.rewards.feature.perk_agricultural"),
        PERK_ECONOMY("feature.city.levels.rewards.feature.perk_economy"),
        TYPE_WAR("feature.city.levels.rewards.feature.type_war"),
        WAR("feature.city.levels.rewards.feature.war"),
        PERK_DREAM("feature.city.levels.rewards.feature.perk_dream"),
        PERK_MILITARY("feature.city.levels.rewards.feature.perk_military"),
        PERK_STRATEGY("feature.city.levels.rewards.feature.perk_strategy");

        private final String nameKey;

        Feature(String nameKey) {
            this.nameKey = nameKey;
        }

        public Component getName() {
            return TranslationManager.translation(nameKey);
        }
    }
}

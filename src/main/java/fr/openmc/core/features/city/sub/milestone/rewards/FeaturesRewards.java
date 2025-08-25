package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

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


    private final Feature[] features;

    FeaturesRewards(Feature... features) {
        this.features = features;
    }

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

    @Getter
    public enum Feature {
        CHEST("§a/city chest"),
        CITY_BANK("§6/city bank"),
        PLAYER_BANK("§b/bank"),
        NOTATION("§3/city notation"),
        RANK("§6/city ranks"),
        MAYOR("§6/city mayor"),
        PERK_AGRICULTURAL("§3les Réformes d'Agriculture"),
        PERK_ECONOMY("§3les Réformes d'Economie"),
        TYPE_WAR("§cle Type de Ville en Guerre"),
        WAR("§c/war"),
        PERK_MILITARY("§3les Réformes Militaires"),
        PERK_STRATEGY("§3les Réformes de Stratégies"),
        ;

        private final String name;

        Feature(String name) {
            this.name = name;
        }
    }
}

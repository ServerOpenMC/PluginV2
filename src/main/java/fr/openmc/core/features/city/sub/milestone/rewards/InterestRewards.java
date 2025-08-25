package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

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


    private final double interest;

    InterestRewards(double interest) {
        this.interest = interest;
    }

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

    @Override
    public Component getName() {
        return Component.text("§7+ §6" + interest * 100 + "% §6d'intérêt");
    }
}

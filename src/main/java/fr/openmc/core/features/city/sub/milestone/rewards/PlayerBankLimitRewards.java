package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

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


    private final Integer bankBalanceLimit;

    PlayerBankLimitRewards(Integer bankBalanceLimit) {
        this.bankBalanceLimit = bankBalanceLimit;
    }

    public static int getBankBalanceLimit(int level) {
        PlayerBankLimitRewards[] values = PlayerBankLimitRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
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

    @Override
    public Component getName() {
        return Component.text("§7Limite à §6" + bankBalanceLimit + " d'Argent §7dans la §bbanque personnelle");
    }
}

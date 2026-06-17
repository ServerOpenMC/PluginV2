package fr.openmc.core.registry.loottable;

import fr.openmc.core.utils.RandomUtils;

public record LootMoney(double money) implements CustomLoot {
    public LootMoney(double minMoney, double maxMoney) {
        this(RandomUtils.randomBetween(minMoney, maxMoney));
    }
}

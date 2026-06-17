package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.entity.Player;


public record MoneyLoot(double money, double getChance) implements CustomLoot {
    public MoneyLoot(double minMoney, double maxMoney, double chance) {
        this(RandomUtils.randomBetween(minMoney, maxMoney), chance);
    }

    @Override
    public void run(Player receiver) {
        EconomyManager.addBalance(receiver.getUniqueId(), money);
    }
}

package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

@Getter
public class MoneyLoot implements CustomLoot, RepresentedItem {
    @Setter
    private double chance;
    private final int money;

    public MoneyLoot(int money, double chance) {
        this.chance = chance;
        this.money = money;
    }

    public MoneyLoot(int minMoney, int maxMoney, double chance) {
        this(RandomUtils.randomBetween(minMoney, maxMoney), chance);
    }

    @Override
    public Component getDisplayText() {
        return Component.text(money, NamedTextColor.GOLD)
                .appendSpace()
                .append(Component.text(EconomyManager.getEconomyIcon()))
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public LootReward run(Player receiver) {
        EconomyManager.addBalance(receiver.getUniqueId(), money);
        return LootReward.loots(Collections.singleton(this));
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return OMCRegistry.CUSTOM_ITEMS.COIN.getBest();
    }
}

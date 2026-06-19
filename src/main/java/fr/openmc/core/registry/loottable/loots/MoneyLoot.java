package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;


public record MoneyLoot(int money, double getChance) implements CustomLoot, RepresentedItem {
    public MoneyLoot(int minMoney, int maxMoney, double chance) {
        this(RandomUtils.randomBetween(minMoney, maxMoney), chance);
    }

    @Override
    public Component getDisplayText() {
        return Component.text(money, NamedTextColor.GOLD)
                .appendSpace()
                .append(Component.text(EconomyManager.getEconomyIcon()));
    }

    @Override
    public Set<CustomLoot> run(Player receiver) {
        EconomyManager.addBalance(receiver.getUniqueId(), money);
        return Collections.singleton(this);
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return OMCRegistry.CUSTOM_ITEMS.COIN.getBest();
    }
}

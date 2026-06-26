package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

@Getter
public class XpLoot implements CustomLoot, RepresentedItem {
    @Setter
    private double chance;
    private final int amountExp;

    public XpLoot(int amountExp, double chance) {
        this.chance = chance;
        this.amountExp = amountExp;
    }

    public XpLoot(int minAmountExp, int maxAmountExp, double chance) {
        this(RandomUtils.randomBetween(minAmountExp, maxAmountExp), chance);
    }

    @Override
    public Component getDisplayText() {
        return Component.text(amountExp + " XP", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public Set<CustomLoot> run(Player receiver) {
        receiver.giveExp(amountExp);
        return Collections.singleton(this);
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return ItemStack.of(Material.EXPERIENCE_BOTTLE);
    }
}

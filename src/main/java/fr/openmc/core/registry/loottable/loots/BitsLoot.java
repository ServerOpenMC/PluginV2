package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.features.bits.BitsManager;
import fr.openmc.core.registry.loottable.LootReward;
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

@Getter
public class BitsLoot implements CustomLoot, RepresentedItem {
    @Setter
    private double chance;
    private final int bits;

    public BitsLoot(int bits, double chance) {
        this.chance = chance;
        this.bits = bits;
    }

    public BitsLoot(int minBits, int maxBits, double chance) {
        this(RandomUtils.randomBetween(minBits, maxBits), chance);
    }

    @Override
    public Component getDisplayText() {
        return Component.text(bits, NamedTextColor.GOLD)
                .appendSpace()
                .append(Component.text(BitsManager.getBitsIcon()))
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public LootReward run(Player receiver) {
        BitsManager.addBits(receiver.getUniqueId(), bits);
        return LootReward.loots(Collections.singleton(this));
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return ItemStack.of(Material.DIAMOND);
    }
}

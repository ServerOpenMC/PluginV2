package fr.openmc.core.features.events.contents.halloween.contents.depositevent;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.deposit.DepositEvent;
import fr.openmc.core.features.events.contents.deposit.NpcSettings;
import fr.openmc.core.registry.loottable.types.rank.RankLootTable;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public class HalloweenPumpkinDepositEvent extends DepositEvent {
    @Override
    public String getId() {
        return "halloween_pumpkin";
    }

    @Override
    public String getTranslationKey() {
        return "feature.events.halloween";
    }

    @Override
    public Prefix getPrefix() {
        return Prefix.HALLOWEEN;
    }

    @Override
    public ItemStack getRequiredItem() {
        return new ItemStack(Material.PUMPKIN);
    }

    @Override
    public int getMainColor() {
        return 0xFF7518;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFFD580;
    }

    @Override
    public NpcSettings getNpcSettings() {
        return new NpcSettings("<gradient:#FF7518:#FFD580>Halloween</gradient>",
                "https://s.namemc.com/i/a4c5cb4636acaa48.png",
                NamedTextColor.GOLD
        );
    }

    @Override
    public LocalDateTime getStartDate() {
        return LocalDateTime.of(2026, 10, 1, 0, 0);
    }

    @Override
    public LocalDateTime getEndDate() {
        return LocalDateTime.of(2026, 11, 1, 0, 0);
    }

    @Override
    public @Nullable RankLootTable getRewards() {
        return OMCRegistry.CUSTOM_LOOT_TABLES.PUMPKIN_DEPOSIT;
    }
}

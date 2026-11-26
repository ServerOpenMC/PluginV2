package fr.openmc.core.features.events.contents.christmas.contents.depositevent;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.deposit.DepositEvent;
import fr.openmc.core.features.events.contents.deposit.NpcSettings;
import fr.openmc.core.registry.loottable.types.rank.RankLootTable;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public class GingerbreadDepositEvent extends DepositEvent {
    @Override
    public String getId() {
        return "christmas_gingerbread";
    }

    @Override
    public String getTranslationKey() {
        return "feature.events.gingerbread";
    }

    @Override
    public Prefix getPrefix() {
        return Prefix.CHRISTMAS;
    }

    @Override
    public ItemStack getRequiredItem() {
        return OMCRegistry.CUSTOM_ITEMS.GINGERBREAD_ITEM.getBest();
    }

    @Override
    public NpcSettings getNpcSettings() {
        return new NpcSettings("<gradient:#FF1B53:#FF9797:#FFD4D4:#FF9797:#FF1B53>Gertrude</gradient>",
                "https://s.namemc.com/i/ac439108099ca164.png",
                NamedTextColor.RED
        );
    }

    @Override
    public @Nullable RankLootTable getRewards() {
        return OMCRegistry.CUSTOM_LOOT_TABLES.GINGERBREAD_DEPOSIT;
    }

    @Override
    public int getMainColor() {
        return 0xFF1B53;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFF9797;
    }

    @Override
    public LocalDateTime getStartDate() {
        return LocalDateTime.of(2026, 12, 1, 0, 0);
    }

    @Override
    public LocalDateTime getEndDate() {
        return LocalDateTime.of(2027, 1, 1, 0, 0);
    }


}

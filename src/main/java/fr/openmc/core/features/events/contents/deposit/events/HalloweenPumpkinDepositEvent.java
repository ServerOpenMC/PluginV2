package fr.openmc.core.features.events.contents.deposit.events;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.deposit.DepositEvent;
import fr.openmc.core.features.events.contents.deposit.DepositReward;
import fr.openmc.core.features.events.contents.deposit.NpcSettings;
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
        return LocalDateTime.of(2026, 10, 31, 1, 0);
    }

    @Override
    public @Nullable DepositReward getReward(int rank, OMCOfflinePlayer player) {
        ItemStack aywenite = OMCRegistry.CUSTOM_ITEMS.AYWENITE.getBest();
        aywenite.setAmount(64);

        return switch (rank) {
            case 1 -> DepositReward.of(30000, createTrophy(Material.PUMPKIN_PIE, 1, player))
                    .with(DepositReward.copies(aywenite, 3));
            case 2 -> DepositReward.of(20000, createTrophy(Material.PUMPKIN_PIE, 2, player))
                    .with(DepositReward.copies(aywenite, 2));
            case 3 -> DepositReward.of(10000, createTrophy(Material.PUMPKIN_PIE, 3, player))
                    .with(DepositReward.copies(aywenite, 1));
            default -> DepositReward.money(3000);
        };
    }
}

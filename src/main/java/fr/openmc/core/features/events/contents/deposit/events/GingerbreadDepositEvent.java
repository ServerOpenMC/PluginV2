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

    @Override
    public @Nullable DepositReward getReward(int rank, OMCOfflinePlayer player) {
        // TODO: a changer
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

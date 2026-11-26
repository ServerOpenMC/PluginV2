package fr.openmc.core.registry.loottable.loots;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.City;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
public class FreeClaimLoot implements CustomLoot {
    @Setter
    private double chance;
    private final int freeClaim;

    public FreeClaimLoot(int freeClaim, double chance) {
        this.chance = chance;
        this.freeClaim = freeClaim;
    }

    public FreeClaimLoot(int minFreeClaim, int maxFreeClaim, double chance) {
        this(RandomUtils.randomBetween(minFreeClaim, maxFreeClaim), chance);
    }

    @Override
    public Component getDisplayText() {
        return Component.text(freeClaim, NamedTextColor.GOLD)
                .appendSpace()
                .append(TranslationManager.translation("feature.city.menus.chunks.free_claim.title"))
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public LootReward run(Player receiver) {
        OMCPlayer omcPlayer = OMCPlayer.of(receiver);

        if (!omcPlayer.city().hasCity()) return LootReward.empty();

        City city = omcPlayer.city().getCity();
        if (city == null) return LootReward.empty();

        city.updateFreeClaims(freeClaim);

        return LootReward.loots(Collections.singleton(this));
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return ItemStack.of(Material.OAK_FENCE);
    }
}

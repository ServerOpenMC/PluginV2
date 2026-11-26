package fr.openmc.core.features.events.contents.deposit;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.types.rank.RankLootTable;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public abstract class DepositEvent {

    public abstract String getId();

    public abstract String getTranslationKey();

    public abstract Prefix getPrefix();

    public abstract LocalDateTime getStartDate();

    public abstract LocalDateTime getEndDate();

    public abstract ItemStack getRequiredItem();

    public abstract NpcSettings getNpcSettings();

    public abstract @Nullable RankLootTable getRewards();

    public Component getName() {
        return TranslationManager.translation(key("name"));
    }

    public String getNpcId() {
        return getId() + "_deposit_npc";
    }

    public int getLeaderboardSize() {
        return 10;
    }

    public int getMainColor() {
        return 0x313131;
    }

    public int getSecondaryColor() {
        return 0xFFFFFF;
    }

    public String key(String suffix) {
        return getTranslationKey() + "." + suffix;
    }

    public @Nullable CustomLootTable getReward(int rank) {
        if (getRewards() == null) return null;

        return getRewards().getReward(rank);
    }

    protected ItemStack createTrophy(Material material, int rank, OfflinePlayer owner) {
        ItemStack item = ItemStack.of(material);
        item.unsetData(DataComponentTypes.CONSUMABLE);
        item.unsetData(DataComponentTypes.FOOD);
        item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getTag(DamageTypeTagKeys.IS_FIRE)));

        item.editMeta(meta -> {
            Component ownerName = owner.getName() == null
                    ? TranslationManager.translation("feature.events.deposit.unknown_player")
                    : Component.text(owner.getName());
            ownerName = ownerName.color(TextColor.color(this.getMainColor())).decoration(TextDecoration.ITALIC, false);
            meta.displayName(TranslationManager.translation(key("reward.rank" + rank + ".name"))
                    .color(TextColor.color(this.getMainColor())));
            meta.lore(TranslationManager.translationLore(key("reward.rank" + rank + ".lore"), ownerName));
        });
        return item;
    }
}

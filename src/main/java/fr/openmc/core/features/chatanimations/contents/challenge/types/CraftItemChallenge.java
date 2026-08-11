package fr.openmc.core.features.chatanimations.contents.challenge.types;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.contents.challenge.ChallengeAnimation;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class CraftItemChallenge extends ChallengeAnimation {
    private final ItemStack item;
    private final int target;
    private final Map<UUID, Integer> progress = new HashMap<>();

    public CraftItemChallenge(CustomItem item, int target, long time) {
        this(item.getBest(), target, OMCRegistry.CUSTOM_LOOT_TABLES.CHALLENGE, time);
    }

    public CraftItemChallenge(ItemStack item, int target, long time) {
        this(item, target, OMCRegistry.CUSTOM_LOOT_TABLES.CHALLENGE, time);
    }

    public CraftItemChallenge(ItemStack item, int target, CustomLootTable reward, long time) {
        super(TranslationManager.translation("feature.chatanimations.challenge.craft_item.name",
                Component.text(target), Component.translatable(item.translationKey())), reward, time);
        this.item = item;
        this.target = target;
    }

    @Override
    public void stop() {
        progress.clear();
    }
}

